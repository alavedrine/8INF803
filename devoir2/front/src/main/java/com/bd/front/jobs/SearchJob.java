package com.bd.front.jobs;

import com.bd.front.SearchSpellApplication;
import com.bd.front.struct.Creature;
import com.bd.front.struct.FilterModel;
import com.bd.front.struct.Spell;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.types.IntegerType;
import scala.collection.mutable.WrappedArray;

import javax.xml.crypto.Data;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Filter;
import java.util.stream.Collectors;

public class SearchJob implements Serializable {
    private final SparkSession sparkSession;
    private final SecureRandom random = new SecureRandom();

    Dataset<Creature> dsCreatures;
    Dataset<Row> dsSpells;

    static FilterModel filterModel = new FilterModel();

    public SearchJob(SparkSession sparkSession)
    {
        this.sparkSession = sparkSession;
    }

    public void startJob(FilterModel filterModel)
    {
        System.out.println(filterModel.toString());

        this.filterModel = filterModel;

        Encoder<Creature> creatureEncoder = Encoders.bean(Creature.class);
        Encoder<Spell> spellEncoder = Encoders.bean(Spell.class);

//        String creaturesJsonpath = SearchSpellApplication.class.getResource("creatures.json").getPath();
//        String spellsJsonPath = SearchSpellApplication.class.getResource("spells.json").getPath();

        String creaturesJsonpath = "C:\\Users\\aymer\\Documents\\Admin\\cours_inge\\S9\\8INF803 BD\\devoir2\\front\\src\\main\\java\\com\\bd\\front\\jobs\\creatures.json";
        String spellsJsonPath = "C:\\Users\\aymer\\Documents\\Admin\\cours_inge\\S9\\8INF803 BD\\devoir2\\front\\src\\main\\java\\com\\bd\\front\\jobs\\spells.json";

        // read JSON file to Dataset
        dsCreatures = sparkSession.read().option("multiline","true").json(creaturesJsonpath).as(creatureEncoder);
        dsSpells = sparkSession.read().option("multiline","true").json(spellsJsonPath);

        dsCreatures.show();
        dsSpells.show();

        FilterFunction<Row> spellFilterFunction = new FilterFunction<Row>() {
            private boolean containsComponent(String component) {
                boolean result = false;
                for (String c : filterModel.getComponents()) {
                    result = result || component.contains(c);
                }
                return result;
            }
            private boolean matchesComponents(WrappedArray<String> components) {
                if (filterModel.getComponents().size() == 0) {
                    return true;
                }
                int n = 0;
                for(String s : (String[]) components.array()) {
                    if (containsComponent(s)) n++;
                }
                return n == components.size();
            }

            private boolean matchesClassesAndLevel(WrappedArray<String> levels) {
                if (filterModel.getClasses().size() == 0) {
                    return true;
                }
                int flag = 0;
                for (String l : (String[]) levels.array()) {
                    String[] splits = l.split(" ");
                    String c = splits[0].toLowerCase();
                    int level = 0;
                    for (String num : splits) {
                        if (isInteger(num)) {
                            level = Integer.parseInt(num);
                            break;
                        }
                    }
                    if (filterModel
                            .getClasses()
                            .stream()
                            .map(String::toLowerCase)
                            .collect(Collectors.toList())
                            .contains(c) && filterModel.getMinLevel() <= level) {
                        if (filterModel.getClassesLogic().equals("OR")) {
                            return true;
                        } else if (filterModel.getClassesLogic().equals("AND")) {
                            flag++;
                        }
                    }
                }
                if (filterModel.getClassesLogic().equals("AND")  && flag != 0) {
                    return flag == filterModel.getClasses().size();
                }
                return false;
            }

            public boolean matchesName(String name) {
                if (filterModel.getName().length() == 0) {
                    return true;
                }
                return name.toLowerCase().contains(filterModel.getName().toLowerCase());
            }

            public boolean matchesKeywords(String name, String description, String castingTime, WrappedArray<String> levels, WrappedArray<String> components) {
                if (filterModel.getKeywords().size() == 0) {
                    return true;
                }
                // add all words from description
                description = description.replaceAll("[.,()\"]", "");
                List<String> textToSearch = Arrays.stream(description.split(" ")).collect(Collectors.toList());

                // add all words from name
                name = name.replaceAll("[.,()\"]", "");
                textToSearch.addAll(Arrays.asList(name.split(" ")));

                // add whole expression from casting time
                textToSearch.add(castingTime);
                // add all words from casting time
                castingTime = castingTime.replaceAll("[.,()\"]", "");
                textToSearch.addAll(Arrays.asList(castingTime.split(" ")));

                // add all words from levels
                for (String s : (String[]) levels.array()) {
                    textToSearch.addAll(Arrays.asList(s.split(" ")));
                }
                for (String s : (String[]) components.array()) {
                    textToSearch.addAll(Arrays.asList(s.split(" ")));
                }
                textToSearch = textToSearch.stream().map(String::toLowerCase).collect(Collectors.toList());

                for (String text : textToSearch) {
                    if (filterModel
                            .getKeywords()
                            .stream()
                            .map(String::toLowerCase)
                            .collect(Collectors.toList())
                            .contains(text)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean call(Row spellRow) throws Exception {
                Spell spell = new Spell(spellRow.getAs("name"),
                        spellRow.getAs("levels"),
                        spellRow.getAs("casting_time"),
                        spellRow.getAs("components"),
                        spellRow.getAs("spell_resistance"),
                        spellRow.getAs("description"));

                return matchesName(spell.name)
                        && matchesComponents(spell.components)
                        && matchesClassesAndLevel(spell.levels)
                        && matchesKeywords(spell.name, spell.description, spell.castingTime, spell.levels, spell.components);
            }
        };

        dsSpells = dsSpells.filter(spellFilterFunction);

        System.out.println("-- RESULTS --");
        System.out.println("Result size = " + dsSpells.count());
        System.out.println("First 20 elements of result");
        Row[] spells = (Row[]) dsSpells.collect();
        for (int i = 0; i < spells.length && i < 20; i++) {
            System.out.println((String) spells[i].getAs("name") + " - "
                    + spells[i].getAs("components"));
        }
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}

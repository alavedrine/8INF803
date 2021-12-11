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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Filter;

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

        String creaturesJsonpath = SearchSpellApplication.class.getResource("creatures.json").getPath();
        String spellsJsonPath = SearchSpellApplication.class.getResource("spells.json").getPath();

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
                int n = 0;
                for(String s : (String[]) components.array()) {
                    if (containsComponent(s)) n++;
                }
                return n == components.size();
            }

            @Override
            public boolean call(Row spellRow) throws Exception {
                Spell spell = new Spell(spellRow.getAs("name"),
                        spellRow.getAs("levels"),
                        spellRow.getAs("casting_time"),
                        spellRow.getAs("components"),
                        spellRow.getAs("spell_resistance"),
                        spellRow.getAs("description"));

                return spell.name.toLowerCase().contains(filterModel.getName().toLowerCase())
                        && matchesComponents(spell.components);
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

    private String nextSessionId()
    {
        return new BigInteger(130, random).toString(32);
    }

    private List<String> getLongList(Long count)
    {
        List<String> lst = new ArrayList<>();
        for (long i = 0; i < count; i++)
        {
            lst.add(nextSessionId());
        }
        return lst;
    }
}

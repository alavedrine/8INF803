package com.bd.front.jobs;

import com.bd.front.SearchSpellApplication;
import com.bd.front.struct.Creature;
import com.bd.front.struct.FilterModel;
import com.bd.front.struct.Spell;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.types.IntegerType;

import javax.xml.crypto.Data;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Filter;

public class SearchJob {




    private final SparkSession sparkSession;
    private final SecureRandom random = new SecureRandom();

    Dataset<Row> dsCreatures;
    Dataset<Row> dsSpells;

    JavaRDD<Row> creatureRDD;
    JavaRDD<Row> spellRDD;

    static FilterModel filterModel = new FilterModel();

    public SearchJob(SparkSession sparkSession)
    {
        this.sparkSession = sparkSession;
    }

    public void startJob(FilterModel filterModel)
    {
        System.out.println(filterModel.toString());

        filterModel = filterModel;

        Encoder<Creature> creatureEncoder = Encoders.bean(Creature.class);
        Encoder<Spell> spellEncoder = Encoders.bean(Spell.class);

        String creaturesJsonpath = SearchSpellApplication.class.getResource("creatures.json").getPath();
        String spellsJsonPath = SearchSpellApplication.class.getResource("spells.json").getPath();

        // read JSON file to Dataset
        dsCreatures = sparkSession.read().option("multiline","true").json(creaturesJsonpath);
        dsSpells = sparkSession.read().option("multiline","true").json(spellsJsonPath);

        dsCreatures.show();
        dsSpells.show();

        creatureRDD = dsCreatures.javaRDD();
        spellRDD = dsSpells.javaRDD();

        FilterModel finalFilterModel = filterModel;
        Function<Row, Boolean> filter = spellRow -> {
            Spell spell = new Spell(spellRow.getAs("name"),
                    spellRow.getAs("levels"),
                    spellRow.getAs("casting_time"),
                    spellRow.getAs("components"),
                    spellRow.getAs("spell_resistance"),
                    spellRow.getAs("description"));

            return spell.name.toLowerCase().contains(finalFilterModel.getName().toLowerCase())
                    && true
                    && true;
                    // Apply every filter here
        };

        spellRDD = spellRDD.filter(filter);

        System.out.println("-- RESULTS --");
        System.out.println("Result size = " + spellRDD.count());
        spellRDD.foreach(spell -> System.out.println(spell.getAs("name").toString()));
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

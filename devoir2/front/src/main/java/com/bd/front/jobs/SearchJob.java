package com.bd.front.jobs;

import com.bd.front.SearchSpellApplication;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchJob {
    public static class Creature implements Serializable {
        public String name;
        public List<String> spells;
    }

    public static class Spell implements Serializable {
        public String name;
        public String level;
        public String castingTime;
        public List<String> components;
        public String spellResistance;
        public String description;
    }

    private final SparkSession sparkSession;
    private final SecureRandom random = new SecureRandom();

    public SearchJob(SparkSession sparkSession)
    {
        this.sparkSession = sparkSession;
    }

    public void startJob()
    {
        Encoder<Creature> creatureEncoder = Encoders.bean(Creature.class);
        Encoder<Spell> spellEncoder = Encoders.bean(Spell.class);

        String creaturesJsonpath = SearchSpellApplication.class.getResource("creatures.json").getPath();
        String spellsJsonPath = SearchSpellApplication.class.getResource("spells.json").getPath();

        // read JSON file to Dataset
        Dataset<Creature> dsCreatures = sparkSession.read().option("multiline","true").json(creaturesJsonpath).as(creatureEncoder);
        Dataset<Spell> dsSpells = sparkSession.read().option("multiline","true").json(spellsJsonPath).as(spellEncoder);

        dsCreatures.show();
        dsSpells.show();
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

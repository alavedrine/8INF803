package com.bd.front.jobs;

import com.bd.front.SearchSpellApplication;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
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

    public Dataset<Spell> dsSpells;
    public Dataset<Creature> dsCreatures;

    public SearchJob(SparkSession sparkSession)
    {
        this.sparkSession = sparkSession;
    }

    public void startJob()
    {
        Encoder<Creature> creatureEncoder = Encoders.bean(Creature.class);
        Encoder<Spell> spellEncoder = Encoders.bean(Spell.class);

//        String spellsJsonPath = SearchSpellApplication.class.getResource("spells.json").getPath();
//        String creaturesJsonpath = SearchSpellApplication.class.getResource("creatures.json").getPath();

//        System.out.println("ABCD " + creaturesJsonpath);

        // read JSON file to Dataset
        dsSpells = sparkSession.read().option("multiline", "true").json("C:\\Users\\aymer\\Documents\\Admin\\cours_inge\\S9\\8INF803 BD\\devoir2\\front\\src\\main\\java\\com\\bd\\front\\jobs\\spells.json").as(spellEncoder);
        dsCreatures = sparkSession.read().option("multiline", "true").json("C:\\Users\\aymer\\Documents\\Admin\\cours_inge\\S9\\8INF803 BD\\devoir2\\front\\src\\main\\java\\com\\bd\\front\\jobs\\creatures.json").as(creatureEncoder);

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

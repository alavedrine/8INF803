package com.bd.front.jobs;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class SearchJob {
    private final JavaSparkContext javaSparkContext;
    private final SecureRandom random = new SecureRandom();

    public SearchJob(JavaSparkContext javaSparkContext)
    {
        this.javaSparkContext = javaSparkContext;
    }

    public void startJob()
    {
        //Step 1 create an array with 1 million random strings
        List<String> longList = getLongList(3000000l);

        //Step 2 parallelize the list
        JavaRDD<String> sparkList = javaSparkContext.parallelize(longList);

        //Step 3 filter the list
        JavaRDD<String> filter = sparkList.filter((String s) ->
        {
            //will check if the string contains the letters ab
            return s.contains("ab");
        });

        System.out.println("number of items in the filter is " + filter.count());

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

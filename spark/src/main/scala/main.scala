import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

object main extends App {
    val conf = new SparkConf()
      .setAppName("Petit exemple")
      .setMaster("local[*]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")


    val tableau: Array[Int] = Array(1,2,3,4,5)

    val resultatRDD = sc.makeRDD(tableau).map( element => {
        element * 3
    }).cache()

    resultatRDD.collect().foreach(  e => print(e))

    println()
    println("*****")
    //On veut garder le plus petit de toute la collection
    val a =  resultatRDD.reduce(   (a, b) => {
        if (a < b) a else b
    })

    println(a)

    val c = resultatRDD.aggregate("")(
        (acc, nouveau) => acc + nouveau.toString,
        (a,b) => a.toString + b.toString
    )
    println(c)

    //Tous les éléments impairs vont avoir la catégorie 2
    //et les pairs vont avoir la catégorie (clé) 1
    //On va créer un PairRDD
    val r1: RDD[(Int, Int)] = resultatRDD.map(elem => {
        var categorie = 1
        if (elem % 2 == 1) categorie = 2
        (categorie, elem)
    })
    r1.foreach(element => println(element))

    r1.reduceByKey(  (a , b ) =>  a+b).collect()
      .foreach( e => println(e))


    println("Resultats du flatmap")

    //3 6 9 12 15
    resultatRDD.flatMap(  elem => {
        val resultats = new ArrayBuffer[(Int,Int)]()
        for (i <- 1 to elem) {
            var categorie = 1
            if (i % 2 == 1) categorie = 2
            resultats += Tuple2(elem, i)
        }
        resultats
    }) .collect(). foreach(  e => println(e))


    //
    //  println("******")
    //  print(c)

}

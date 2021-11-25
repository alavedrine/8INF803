import Healing.df
import org.apache.spark
import org.apache.spark.rdd.PairRDDFunctions
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.functions.{col, collect_list, explode_outer}
import org.apache.spark.SparkContext._

import scala.collection.mutable

object Healing extends App {
    case class Creature(name: String, spells: Array[String])
    case class Spell(spell: String, creatures: Array[String])

    val spark = SparkSession
    .builder()
    .appName("Healing")
    .master("local[*]")
    .getOrCreate()

    val savePath = "src/main/reversed_creatures"

    import spark.implicits._
    val df = spark.read.option("multiline", "true").json("src/main/creatures.json").as[Creature]
    val reversedDF = df.select($"name",explode_outer($"spells").as("spell"))

    println("DataFrame version :")
    dataframeVersion(reversedDF)

    println("RDD Version")
   // rddVersion(reversedDF)

    def dataframeVersion(df: DataFrame): Unit = {
        val mergedReversedDf = df.groupBy($"spell").agg(collect_list($"name").as("creatures")).as[Spell]

        mergedReversedDf.foreach(row => {
            print("Spell : " + row.spell + "  Creatures : ")
            row.creatures.foreach(creature => print(creature + "; "))
            println()
        })

       // mergedReversedDf.rdd.saveAsTextFile(savePath)
    }

    // Ne fonctionne pas
    def rddVersion(df: DataFrame): Unit = {
        val rdd = df.rdd.groupBy(c => c.get(1)).mapValues(_.toArray)

        rdd.foreach(row => {
            print("Spell : " + row._1 + " Creatures : ")
            row._2.foreach(creature => print(creature + ";"))
            println()
        })

       // rdd.saveAsTextFile(savePath)
    }
}

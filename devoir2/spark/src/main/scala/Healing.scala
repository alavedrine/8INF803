import Healing.df
import org.apache.spark
import org.apache.spark.rdd.PairRDDFunctions
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, collect_list, collect_set, explode_outer}
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

    val savePath = "src/main/reversed_creatures/"

    import spark.implicits._
    val df = spark.read.option("multiline", "true").json("src/main/creatures.json").as[Creature]
    val reversedDF = df.select($"name",explode_outer($"spells").as("spell"))

    println("DataFrame version :")
    val merged = dataframeVersion(reversedDF)
    merged.write.format("json").save("src/main/reversed_creatures/")


    def dataframeVersion(df: DataFrame): Dataset[Spell] = {
        val mergedReversedDf = df.groupBy($"spell").agg(collect_list($"name").as("creatures")).as[Spell]

        mergedReversedDf.foreach(row => {
            print("Spell : " + row.spell + "  Creatures : ")
            row.creatures.foreach(creature => print(creature + "; "))
            println()
        })
        return mergedReversedDf
//        mergedReversedDf.write.json(savePath)
//        mergedReversedDf
//          .write
//          .format("json")
//          .save(savePath)
//        mergedReversedDf.rdd.repartition(1).saveAsTextFile(savePath)
    }

}

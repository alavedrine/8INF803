import Healing.{Creature, Spell, dataframeVersion, df, spark}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{collect_list, explode_outer}

object Check extends App {
    case class FullSpell(name: String, levels: Array[String], casting_time: String, components: Array[String], spell_resistance: String, description: String)

    val spark = SparkSession
      .builder()
      .appName("Check")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._
    val df = spark.read.option("multiline", "true").json("src/main/creatures.json").as[Creature]
    val reversedDF = df.select($"name",explode_outer($"spells").as("spell"))

    val mergedReverseDf = reversedDF.groupBy($"spell").agg(collect_list($"name").as("creatures")).as[Spell]

    val spells = spark.read.option("multiline", "true").json("src/main/spells.json").as[FullSpell]

//    spells.show()
//    mergedReverseDf.show()

//    val spell_names = mergedReverseDf.select("spell").rdd.map(r => r(0)).collect()
//    spells.filter(spells("name").isin(spell_names:_*)).show()
    val spell_names = spells.select("name").rdd.map(r => r(0).toString.toLowerCase()).collect()
    mergedReverseDf.filter(!mergedReverseDf("spell").isin(spell_names:_*)).show()

}

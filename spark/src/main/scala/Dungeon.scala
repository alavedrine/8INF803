import org.apache.spark
import org.apache.spark.sql.SparkSession

object Dungeon extends App {
    case class Spell(casting_time: String, components: Array[String], description: String, level: String, name: String, spell_resistance: String)
    val spark = SparkSession
    .builder()
    .appName("Dungeon")
    .master("local[*]")
    .getOrCreate()
    import spark.implicits._
    val df = spark.read.option("multiline", "true").json("src/main/spells.json").as[Spell]

    filterWizard()
    sqlWizard()

    def filterWizard(): Unit = {
        val result = df.filter(spell => (spell.components contains "V") && (spell.components.length == 1)).collect()

        println("Length : " + result.length)
        result.foreach(row => println(row.name))
    }

    def sqlWizard(): Unit = {
        df.createOrReplaceTempView("spells")
        val sql = spark.sql("SELECT * FROM spells " +
          "WHERE size(spells.components) = 1 " +
          "AND array_contains(components, 'V')").collect()

        println("Length : " + sql.length)
        sql.foreach(row => println(row))
    }
}

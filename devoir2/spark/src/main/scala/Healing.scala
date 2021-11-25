import org.apache.spark.sql.SparkSession

object Healing extends App {
  case class Creature(name: String, spells: Array[String])
  val spark = SparkSession
    .builder()
    .appName("Healing")
    .master("local[*]")
    .getOrCreate()
  import spark.implicits._
  val df = spark.read.option("multiline", "true").json("src/main/creatures.json").as[Creature]

  
}

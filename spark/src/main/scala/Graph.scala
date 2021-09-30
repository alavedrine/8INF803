import org.apache.spark.{SparkConf, SparkContext}

object Graph extends App {
    val conf = new SparkConf().setMaster("local[*]").setAppName("Graph")
    val sc = new SparkContext(conf)

    sc.setLogLevel("ERROR")

    val mots = Array("Edmond", "Jimmy", "Theo", "Edmond", "Aurelien", "Jimmy")
    val rdd = sc.makeRDD(mots)

    val transformation1 = rdd.map(e => (e, 1))
    val transformation2 = transformation1.reduceByKey((a, b) => a + b)
    val result = transformation2.collect().foreach(e => println(e))
}


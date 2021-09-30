import org.apache.spark.sql.SparkSession

object PageRank extends App {
    val spark = SparkSession
      .builder()
      .appName("PageRank")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val graph = spark.sparkContext.parallelize(List(("A", List("B", "C")), ("B", List("C")), ("C", List("A")), ("D", List("C")), ("E", List("C"))))
    var ranks = graph.mapValues(_ => 1.0)

    val iteration = 20
    val dampingFactor = 0.8
    for(i <- 1 to iteration) {
        println("Iteration : " + i)
        val contributions = graph.join(ranks).flatMap { case (url, (links, rank)) => links.map(dest => (dest, rank / links.size)) }
        val newRanks = contributions.reduceByKey((x, y) => x + y).mapValues(v => (1 - dampingFactor) + dampingFactor*v)
        var inter = ranks.subtractByKey(newRanks)
        if (inter.count() != 0) {
            inter = inter.mapValues(v => 1 - dampingFactor)
        }
        ranks = newRanks.union(inter)
        println(ranks.collect().foreach(elem => print(elem.toString())))
    }
}
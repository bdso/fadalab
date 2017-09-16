///**
//  * Created by leo on 7/7/17.
//  */
//
//import org.apache.log4j.{Level, Logger}
//import org.apache.spark.{SparkConf, SparkContext}
//import org.apache.spark.SparkContext._
//import org.apache.spark.rdd.RDD
//import org.apache.spark.streaming.Duration
//import org.elasticsearch.spark._
//import org.elasticsearch.spark.rdd.EsSpark
//
//object ElasticExample {
//
//  private val appName = "ElasticExample"
//  //  private val windowSize = Duration(60000L) // 60 seconds
//  //  private val slidingInterval = Duration(10000L) // 10 seconds
//  private val checkpointInterval = Duration(20000L) // 20 seconds
//  private val checkpointDir = "./checkpoint/fastdata"
//
//
//  def main(args: Array[String]): Unit = {
//
//    Logger.getLogger("org").setLevel(Level.OFF)
//    Logger.getLogger("akka").setLevel(Level.OFF)
//
//    val conf = new SparkConf()
//      .setAppName(appName)
//      .setMaster("local[2]")
//
//    conf.set("es.index.auto.create", "true")
//
//    val sc = new SparkContext(conf)
//
//    val numbers = Map("one" -> 1, "two" -> 2, "three" -> 3)
//    val airports = Map("arrival" -> "Otopeni", "SFO" -> "San Fran")
//    val rdd = sc.makeRDD(Seq(numbers, airports))
//    EsSpark.saveToEs(rdd, "spark/docs")
//    println("Done Writing.")
//
//    val RDD = sc.esRDD("spark/docs")
//    println(RDD.count())
//
//    println("Done Reading.")
//
//  }
//
//}
//
//
//

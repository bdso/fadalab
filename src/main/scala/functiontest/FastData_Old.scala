package functiontest


/**
  * Created by leo on 3/31/17.
  */

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkContext, _}
import org.apache.spark.streaming.{StreamingContext, _}
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.elasticsearch.spark.streaming._

//Logger.getLogger(classOf[RackResolver]).getLevel


object FastData_Old {

  private val appName = "FastData"
  //  private val windowSize = Duration(60000L) // 60 seconds
  //  private val slidingInterval = Duration(10000L) // 10 seconds
  private val checkpointInterval = Duration(20000L) // 20 seconds
  private val checkpointDir = "./checkpoint/fastdata"

  def main(args: Array[String]): Unit = {

    // Create a local StreamingContext with two working thread and batch interval of 1 second.
    // The master requires 2 cores to prevent from a starvation scenario.

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val conf = new SparkConf()
      .setAppName(appName)
      .setMaster("local[2]")

    conf.set("es.index.auto.create", "true")
    //    conf.set("es.nodes", "i1")
    //    conf.set("es.port", "9200")

    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(1))

    val lines: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 9999)
    lines.print()
    //
    //    //    Start Test Stream Elasticsearch
    //    //    val numbers ="""{"reason" : "business", "airport" : "SFO"}"""
    //    //    val rdd = sc.makeRDD(Seq(numbers)"""
    //    //    val rdd = sc.makeRDD(Seq(numb)
    //    //    val microbatches = mutable.Queue(rdd)
    //    //    val dstream = ssc.queueStream(microbatches)
    //    //    EsSparkStreaming.saveJsonToEs(dstream, "spark/docs")
    //    //    End Test Stream Elasticsearch
    //
    //    EsSparkStreaming.saveJsonToEs(lines, "spark/docs")

    /**
      * DStream socket.
      */

    //    val lines = ssc.textFileStream("hdfs://10.10.14.83:9000/etl/BI/speedlayer/nginxstreams/2017/nginxstreams-201-0-1483685742911")
    //    val lines = ssc.textFileStream("hdfs://10.10.14.84:9000/etl/BI/speedlayer/nginxstreams/2017/*")
    //    val lines = ssc.textFileStream("hdfs://10.10.14.84:9000/etl/BI/speedlayer/nginxapps/2017/appsfa-13-1115-1499184442564")
    //    val lines = ssc.textFileStream("hdfs://10.10.14.84:9000/tmp/")
    //    lines.print()
    //    EsSparkStreaming.saveJsonToEs(lines, "spark/docs")

    val index = "ngx-"
    val _type = "docs"
    val indexType = index + "/" + _type

    val dStream = lines.map(s => s)

    //    EsSparkStreaming.saveJsonToEs(lines, indexType)
    EsSparkStreaming.saveJsonToEs(dStream, indexType)

    /**
      * Reduce last 30 seconds of data, every 10 seconds
      *
      * WordStream.reduceByKeyAndWindow((x: Int, y: Int) => x+y, windowSize, slidingInterval)
      */

    //    val windowedWordCounts = pairs.reduceByKeyAndWindow((a: Int, b: Int) => (a + b), windowSize, slidingInterval)
    //    wordCounts.print()

    //    windowedWordCounts.print()

    ssc.checkpoint(checkpointDir)

    ssc.start() // Start the computation
    ssc.awaitTermination() // Wait for the computation to terminate

  }

}

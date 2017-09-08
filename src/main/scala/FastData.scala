
/**
  * Created by leo on 3/31/17.
  */


import java.io.File

import com.typesafe.config.ConfigFactory
import function.FunctionStream._
import function._
import mapping.Object._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming._
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.{SparkConf, SparkContext}


object FastData {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    if (args.length < 1) {
      System.err.println(
        s"""
           |Usage: FastData <config>
           |  <config> is a config file
           |
            """.stripMargin)
      System.exit(1)
    }

    val loadConfig = new Settings(ConfigFactory.parseFile(new File(args(0))))

    val conf = new SparkConf()
      .setAppName(loadConfig.setAppName)
      .setMaster(loadConfig.setMaster)

    conf.set("es.index.auto.create", loadConfig.setIndexAutoCreate)
    conf.set("es.nodes", loadConfig.setEsNodes)
    conf.set("es.port", loadConfig.setEsPort)

    val sc = new SparkContext(conf)
    val ssc: StreamingContext = new StreamingContext(sc, Seconds(loadConfig.setSlidingInterval))

    val kafkaConfig = KafkaStreamObj(loadConfig.setTopics, loadConfig.setBrokers, ssc)
    val dStream: DStream[AppObj] = kafkaDirectStream(kafkaConfig)

    // Get the lines, split them into words, count the words and print
    //    val dStream: DStream[ESObj] = lines.map { i =>
    //      val obj = ESObj(i.toString)
    //      obj
    //    }

    val elasticConfig = ElasticStreamObj(loadConfig.setEsIndexName, loadConfig.setEsType, dStream)
    storeEsSparkStream(elasticConfig)
    dStream.print()

    /**
      * Start the computation.
      **/

    ssc.checkpoint(loadConfig.setCheckpointDir)

    ssc.start() // Start the computation
    ssc.awaitTermination() // Wait for the computation to terminate

  }

}

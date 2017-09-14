
/**
  * Created by leo on 3/31/17.
  */


import java.io.File
import java.time.LocalDateTime

import com.typesafe.config.ConfigFactory
import function.FunctionStream._
import function.SetConfig._
import function._
import mapping.Object._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.streaming._

object FastData {

  val LOG: Logger = Logger.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    if (args.length < 1) {
      LOG.info("*** Start load config ***")
      System.err.println(
        s"""
           |Usage: FastData <config>
           |  <config> is a config file
           |
            """.stripMargin)
      System.exit(1)
    }

    val loadFileConfig = ConfigFactory.parseFile(new File(args(0)))
    val loadConfig = new Settings(ConfigFactory.load(loadFileConfig))

    //    val conf: SparkConf = new SparkConf()
    //      .setAppName(loadConfig.getAppName)
    //      .setMaster(loadConfig.getMaster)

    //    setConfig(conf, loadConfig)
    val conf = updateConfigFromFile(loadFileConfig)

    val sc = new SparkContext(conf)
    val ssc: StreamingContext = new StreamingContext(sc, Seconds(loadConfig.getSlidingInterval))

    // Load data from kafka.
    val kafkaConfig = KafkaStreamObj(loadConfig.getTopics, loadConfig.getBrokers, ssc)
    val dStreamKafka = kafkaDirectStream(kafkaConfig)

    // Store into elasticsearch.
    val elasticConfig = ElasticStreamObj(loadConfig.getEsIndexName, loadConfig.getEsType, dStreamKafka)
    storeEsSparkStream(elasticConfig)

    // Calculator msg.
    val dStream = dStreamKafka.map(x => (x.hostsource, 1L)).reduceByKey(_ + _)
      .reduceByKeyAndWindow(_ + _, _ - _, Seconds(loadConfig.getWindowSize), Seconds(loadConfig.getSlidingInterval))
    //    dStream.print()

    //    Store into redis.
    storeRedisSparkStream(dStream)
    val dStreamCassandra = dStream.map(x => (LocalDateTime.now.toString, x._1, x._2))
    dStreamCassandra.print()

    // Store into Cassandra.
    //    dStreamCassandra.saveToCassandra(loadConfig.getCassandraKeyspace, "ccu", SomeColumns("time", "keys", "counts"))

    //    dStreamCassandra.print()

    /**
      * Start the computation.
      **/

    ssc.checkpoint(loadConfig.getCheckpointDir)

    ssc.start() // Start the computation
    ssc.awaitTermination() // Wait for the computation to terminate

  }

}

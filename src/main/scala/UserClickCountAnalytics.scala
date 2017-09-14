import java.io.File

import com.typesafe.config.ConfigFactory
import function.FunctionStream.kafkaDirectStream
import function.Settings
import function.redis.RedisClient
import mapping.Object.KafkaStreamObj
import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.dstream.DStream.toPairDStreamFunctions
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import redis.clients.jedis.Jedis

object UserClickCountAnalytics {

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
      .setAppName(loadConfig.getAppName)
      .setMaster(loadConfig.getMaster)

    val sc = new SparkContext(conf)
    val ssc: StreamingContext = new StreamingContext(sc, Seconds(loadConfig.getSlidingInterval))

    // Load data from kafka.
    val kafkaConfig = KafkaStreamObj(loadConfig.getTopics, loadConfig.getBrokers, ssc)
    val events = kafkaDirectStream(kafkaConfig)


    val dbIndex = 1
    val clickHashKey = "app::users::click"
    // Compute user click times
    val userClicks = events.map(x => (x.hostsource, 1L)).reduceByKey(_ + _)
    userClicks.foreachRDD(rdd => {
      rdd.foreachPartition(partitionOfRecords => {
        partitionOfRecords.foreach(f = pair => {

          /**
            * Internal Redis client for managing Redis connection {@link Jedis} based on {@link RedisPool}
            */

          val uid = pair._1
          val clickCount = pair._2
          val jedis: Jedis = RedisClient.pool.getResource
//          jedis.select(dbIndex)
          jedis.hincrBy(clickHashKey, uid, clickCount)
          RedisClient.pool.returnResource(jedis)
        })
      })
    })

    userClicks.print()
    ssc.start()
    ssc.awaitTermination()

  }
}
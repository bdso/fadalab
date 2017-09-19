package function

import java.time.LocalDateTime
import java.util.UUID

//import com.datastax.spark.connector.streaming._
import com.datastax.spark.connector.streaming._
import function.redis.RedisClient
import kafka.serializer.StringDecoder
import mapping.Object._
import net.liftweb.json._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.elasticsearch.spark.streaming.EsSparkStreaming

object FunctionStream {

  implicit val formats = DefaultFormats
  val LOG: Logger = Logger.getLogger(this.getClass)

  def getFpId(publicIp: String, userAgent: String): UUID = {
    val s = publicIp + userAgent
    val fpId = UUID.nameUUIDFromBytes(s.trim().getBytes())
    fpId
  }

  //  def getFpId(publicIp: String, userAgent: String): String = {
  //    val s = publicIp + userAgent
  //    val fpId = UUID.nameUUIDFromBytes(s.trim().getBytes())
  //    fpId.toString
  //  }


  def application(conf: SparkConf, loadConfig: Settings) = {

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val sc = new SparkContext(conf)
    val ssc: StreamingContext = new StreamingContext(sc, Seconds(loadConfig.getSlidingInterval))

    // Load data from kafka.
    val kafkaConfig = KafkaStreamObj(loadConfig.getTopics, loadConfig.getBrokers, ssc)
    val dStreamKafka = kafkaDirectStream(kafkaConfig)
    dStreamKafka.print()

    val dStreamTmp: DStream[Click] = dStreamKafka.map(rdd => {
      val fpId = getFpId(rdd.private_ip, rdd.user_agent)
      val clicks = Click(rdd.id, rdd.click_id, rdd.product_id, rdd.timestamp, rdd.link_id, rdd.dns_ip, rdd.public_ip, rdd.private_ip,
        rdd.referrer, rdd.user_agent, rdd.isp, rdd.country, rdd.city, rdd.language, rdd.os, rdd.browser, rdd.time_zone,
        rdd.screen_size, rdd.fonts, rdd.http_params, rdd.campaign_id, rdd.channel_id, rdd.zone_id, rdd.utm_source,
        rdd.utm_campaign, rdd.utm_medium, rdd.utm_term, rdd.network, rdd.future_params, fpId)
      clicks
    })
    dStreamTmp.print()
    // Calculator msg.
    //    val dStream = dStreamKafka.map(x => (x.click_id, 1L)).reduceByKey(_ + _)
    //      .reduceByKeyAndWindow(_ + _, _ - _, Seconds(loadConfig.getWindowSize), Seconds(loadConfig.getSlidingInterval))
    //    dStream.print()

    //    Store into redis.
    //    storeRedisSparkStream(dStream)
    //    val dStreamCassandra = dStream.map(x => (LocalDateTime.now.toString, x._1, x._2))
    //    dStreamCassandra.print()

    // Store into Cassandra.
    dStreamTmp.saveToCassandra(loadConfig.getCassandraKeyspace, "clicks")
    dStreamTmp.print()
    //        dStreamCassandra.saveToCassandra(loadConfig.getCassandraKeyspace, "ccu", SomeColumns("time", "keys", "counts"))
    //        dStreamCassandra.print()

    /**
      * Start the computation.
      **/

    ssc.checkpoint(loadConfig.getCheckpointDir)

    ssc.start() // Start the computation
    ssc.awaitTermination() // Wait for the computation to terminate
  }

  def appRedisStream(conf: SparkConf, loadConfig: Settings) = {


    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val sc = new SparkContext(conf)
    val ssc: StreamingContext = new StreamingContext(sc, Seconds(loadConfig.getSlidingInterval))

    // Load data from kafka.
    val kafkaConfig = KafkaStreamObj(loadConfig.getTopics, loadConfig.getBrokers, ssc)
    val dStreamKafka = kafkaDirectStream(kafkaConfig)

    // Calculator msg.
    val dStream = dStreamKafka.map(x => (x.user_agent, 1L)).reduceByKey(_ + _)
      .reduceByKeyAndWindow(_ + _, _ - _, Seconds(loadConfig.getWindowSize), Seconds(loadConfig.getSlidingInterval))
    dStream.print()

    //    Store into redis.
    storeRedisSparkStream(dStream)
    val dStreamCassandra = dStream.map(x => (LocalDateTime.now.toString, x._1, x._2))
    dStreamCassandra.print()

    /**
      * Start the computation.
      **/

    ssc.checkpoint(loadConfig.getCheckpointDir)

    ssc.start() // Start the computation
    ssc.awaitTermination() // Wait for the computation to terminate

  }

  def appElasticStream(conf: SparkConf, loadConfig: Settings) = {

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val sc = new SparkContext(conf)
    val ssc: StreamingContext = new StreamingContext(sc, Seconds(loadConfig.getSlidingInterval))

    // Load data from kafka.
    val kafkaConfig = KafkaStreamObj(loadConfig.getTopics, loadConfig.getBrokers, ssc)
    val dStreamKafka = kafkaDirectStream(kafkaConfig)

    // Store into elasticsearch.
    //    val elasticConfig = ElasticStreamObj(loadConfigdConfig.getEsIndexName, loadConfig.getEsType, dStreamKafka)
    //    storeEsSparkStream(elasticConfig)

    /**
      * Start the computation.
      **/

    ssc.checkpoint(loadConfig.getCheckpointDir)

    ssc.start() // Start the computation
    ssc.awaitTermination() // Wait for the computation to terminate
  }

  def kafkaDirectStream(kafkaConfig: KafkaStreamObj) = {
    val topicsSet: Set[String] = kafkaConfig.topics.split(",").toSet
    val kafkaParams: Map[String, String] = Map[String, String]("metadata.broker.list" -> kafkaConfig.broker)
    val messages: InputDStream[(String, String)] = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      kafkaConfig.scc, kafkaParams, topicsSet)
    val dataOut = messages.map(msg => parse(msg._2).extract[ObjReceive])
    dataOut
  }

  def storeEsSparkStream(elasticConfig: ElasticStreamObj) = {
    val indexType = elasticConfig.index + "/" + elasticConfig._type
    EsSparkStreaming.saveToEs(elasticConfig.dStream, indexType)
  }

  def storeEsSparkStream(elasticConfig: ElasticStreamJson) = {
    val indexType = elasticConfig.index + "/" + elasticConfig._type
    EsSparkStreaming.saveToEs(elasticConfig.dStream, indexType)
  }

  def storeEsSparkStream(loadConfig: Settings, dStream: DStream[AppObj]) = {
    val indexType = loadConfig.getEsIndexName + "/" + loadConfig.getEsType
    EsSparkStreaming.saveToEs(dStream, indexType)
  }

  def storeRedisSparkStream(dStream: DStream[(String, Long)]) = {
    dStream.foreachRDD(rdd => {
      rdd.foreachPartition(partitionOfRecords => {
        partitionOfRecords.foreach(pair => {
          val uid = pair._1
          val clickCount = pair._2
          val jedis = RedisClient.pool.getResource
          jedis.select(RedisClient.dbIndex)
          jedis.hincrBy(RedisClient.clickHashKey, uid, clickCount)
          RedisClient.pool.returnResource(jedis)
        })
      })
    })
  }

}

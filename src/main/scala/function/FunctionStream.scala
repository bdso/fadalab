package function

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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

  def conTimeStoreCassandra(date: String) = {
    val fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
    val fmt2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val localDate = LocalDateTime.parse(date, fmt1)
    val time = fmt2.format(localDate)
    time
  }

  def comTimeActiveUser(date1: String, date2: String) = {
    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val localDate1 = LocalDateTime.parse(date1, fmt)
    val localDate2 = LocalDateTime.parse(date2, fmt)
    val days = ChronoUnit.DAYS.between(localDate1, localDate2)
    days.toInt
  }


  def application(conf: SparkConf, loadConfig: Settings) = {

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val sc = new SparkContext(conf)
    val ssc: StreamingContext = new StreamingContext(sc, Seconds(loadConfig.getSlidingInterval))

    // Load data from kafka.
    val kafkaConfig = KafkaStreamObj(loadConfig.getTopics, loadConfig.getBrokers, ssc)
    val dStreamKafka = kafkaDirectStream(kafkaConfig)
    dStreamKafka.print()

    val dStreamClick: DStream[Click] = addFpId(dStreamKafka)
    val dStreamActive: DStream[ActiveUser] = addActive(dStreamClick)

    // Store into Cassandra.
    //    storeCassandraSparkStream(dStreamClick, loadConfig.getCassandraKeyspace, "raw_clicks")
    //    dStreamClick.print()

    storeCassandraSparkStream(dStreamActive, loadConfig.getCassandraKeyspace, "raw_clicks")
    dStreamActive.print()

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

  def storeRedisSparkStream(dStream: DStream[(String, Long)]): Unit = {
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

  def getActiveUserRedisSparkStream(fpId: String, timestamp: String): Int = {
    var active = 0
    val jedis = RedisClient.pool.getResource
    jedis.select(RedisClient.dbIndex)
    if (jedis.hexists(RedisClient.clickHashKey, fpId)) {
      val timestampTmp = jedis.hget(RedisClient.clickHashKey, fpId)
      active = comTimeActiveUser(timestampTmp, timestamp)
    } else {
      jedis.hset(RedisClient.clickHashKey, fpId, timestamp)
    }
    RedisClient.pool.returnResource(jedis)
    active
  }

  //  def storeCassandraSparkStream(dStream: DStream[Click], keyspace: String, tableName: String) = {
  def storeCassandraSparkStream(dStream: DStream[ActiveUser], keyspace: String, tableName: String) = {
    dStream.saveToCassandra(keyspace, tableName)
  }

  def addFpId(dStream: DStream[ObjReceive]) = {
    dStream.map(rdd => {
      val fpId = getFpId(rdd.private_ip, rdd.user_agent)

      //      val clicks = Click(UUID.fromString(rdd.id),
      val clicks = Click(UUID.randomUUID(),
        UUID.fromString(rdd.click_id),
        UUID.fromString(rdd.product_id),
        conTimeStoreCassandra(rdd.timestamp),
        UUID.fromString(rdd.link_id),
        rdd.dns_ip,
        rdd.public_ip,
        rdd.private_ip,
        rdd.referrer,
        rdd.user_agent,
        rdd.isp,
        rdd.country,
        rdd.city,
        rdd.language,
        rdd.os,
        rdd.browser,
        rdd.time_zone,
        rdd.screen_size,
        rdd.fonts,
        rdd.http_params,
        UUID.fromString(rdd.campaign_id),
        UUID.fromString(rdd.channel_id),
        UUID.fromString(rdd.zone_id),
        rdd.utm_source,
        rdd.utm_campaign,
        rdd.utm_medium,
        rdd.utm_term,
        rdd.network,
        rdd.future_params,
        fpId)
      clicks
    })
  }

  def addActive(dStream: DStream[Click]) = {
    dStream.map(rdd => {
      val active = getActiveUserRedisSparkStream(rdd.fp_id.toString, rdd.timestamp)
      val activeUser = ActiveUser(rdd.id,
        rdd.click_id,
        rdd.product_id,
        rdd.timestamp,
        rdd.link_id,
        rdd.dns_ip,
        rdd.public_ip,
        rdd.private_ip,
        rdd.referrer,
        rdd.user_agent,
        rdd.isp,
        rdd.country,
        rdd.city,
        rdd.language,
        rdd.os,
        rdd.browser,
        rdd.time_zone,
        rdd.screen_size,
        rdd.fonts,
        rdd.http_params,
        rdd.campaign_id,
        rdd.channel_id,
        rdd.zone_id,
        rdd.utm_source,
        rdd.utm_campaign,
        rdd.utm_medium,
        rdd.utm_term,
        rdd.network,
        rdd.future_params,
        rdd.fp_id,
        active)

      activeUser
    })
  }

}

package function

import java.time.LocalDateTime

import function.redis.RedisClient
import kafka.serializer.StringDecoder
import mapping.Object._
import net.liftweb.json._
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.elasticsearch.spark.streaming.EsSparkStreaming

object FunctionStream {

  implicit val formats = DefaultFormats

  def application(conf: SparkConf, loadConfig: Settings) = {
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

  def kafkaDirectStream(kafkaConfig: KafkaStreamObj) = {
    val topicsSet: Set[String] = kafkaConfig.topics.split(",").toSet
    val kafkaParams: Map[String, String] = Map[String, String]("metadata.broker.list" -> kafkaConfig.broker)
    val messages: InputDStream[(String, String)] = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      kafkaConfig.scc, kafkaParams, topicsSet)
    val dataOut = messages.map(msg => parse(msg._2).extract[AppObjOver])
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
        partitionOfRecords.foreach(f = pair => {
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

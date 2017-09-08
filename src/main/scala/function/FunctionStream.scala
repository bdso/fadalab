package function

import kafka.serializer.StringDecoder
import mapping.Object._
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.elasticsearch.spark.streaming.EsSparkStreaming
import play.api.libs.json.Json

object FunctionStream {

  implicit val appFormat = Json.format[AppObj]

  def kafkaDirectStream(kafkaConfig: KafkaStreamObj) = {
    val topicsSet: Set[String] = kafkaConfig.topics.split(",").toSet
    val kafkaParams: Map[String, String] = Map[String, String]("metadata.broker.list" -> kafkaConfig.broker)
    val messages: InputDStream[(String, String)] = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      kafkaConfig.scc, kafkaParams, topicsSet)
    val dataOut = messages.map(msg => Json.parse(msg._2).as[AppObj])
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
    val indexType = loadConfig.setEsIndexName + "/" + loadConfig.setEsType
    EsSparkStreaming.saveToEs(dStream, indexType)
  }


}

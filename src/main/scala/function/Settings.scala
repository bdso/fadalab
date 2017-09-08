package function

import com.typesafe.config.Config

class Settings(config: Config) {

  /**
    * Non-lazy fields.
    */

  val setAppName = config.getString("spark.app.name")
  val setMaster = config.getString("spark.master")
  val setSlidingInterval = config.getInt("spark.slidingInterval")
  val setWindowSize = config.getInt("spark.windowSize")
  val setCheckpointDir = config.getString("spark.checkpointDir")

  val setBrokers = config.getString("spark.kafka.brokers")
  val setTopics = config.getString("spark.kafka.topics")
  val setParams = config.getString("spark.kafka.params.serializer-class")

  val setIndexAutoCreate = config.getString("spark.es.index.auto.create")
  val setEsNodes = config.getString("spark.es.nodes")
  val setEsPort = config.getString("spark.es.port")
  val setEsIndexName = config.getString("spark.es.index.name")
  val setEsType = config.getString("spark.es.type")

}


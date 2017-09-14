package function

import com.typesafe.config.Config

class Settings(config: Config) {

  /**
    * Non-lazy fields.
    */

  val getAppName = config.getString("spark.app.name")
  val getMaster = config.getString("spark.master")
  val getSlidingInterval = config.getInt("spark.slidingInterval")
  val getWindowSize = config.getInt("spark.windowSize")
  val getCheckpointDir = config.getString("spark.checkpointDir")

  val getBrokers = config.getString("spark.kafka.brokers")
  val getTopics = config.getString("spark.kafka.topics")
  val getParams = config.getString("spark.kafka.params.serializer-class")

  val getIndexAutoCreate = config.getString("spark.es.index.auto.create")
  val getEsNodes = config.getString("spark.es.nodes")
  val getEsPort = config.getString("spark.es.port")
  val getEsIndexName = config.getString("spark.es.index.name")
  val getEsType = config.getString("spark.es.type")

  val getRedisHosts = config.getString("spark.redis.hosts")
  val getRedisPort = config.getInt("spark.redis.port")

  val getCassandraHosts = config.getString("spark.cassandra.connection.host")
  val getCassandraPort = config.getString("spark.cassandra.connection.port")
  val getCassandraKeyspace = config.getString("spark.cassandra.connection.keyspace")
  val getCassandraUsername = config.getString("spark.cassandra.auth.username")
  val getCassandraPassword = config.getString("spark.cassandra.auth.password")

}


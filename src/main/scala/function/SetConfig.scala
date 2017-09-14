package function

import com.typesafe.config.Config
import org.apache.spark.SparkConf

object SetConfig {

  def setConfig(conf: SparkConf, loadConfig: Settings) = {
    // Opz.
    conf.set("spark.worker.cleanup.enabled", "true")
    // Config for elasticsearch.
    conf.set("es.index.auto.create", loadConfig.getIndexAutoCreate)
    conf.set("es.nodes", loadConfig.getEsNodes)
    conf.set("es.port", loadConfig.getEsPort)
    // Config for cassandra.
    conf.set("spark.cassandra.connection.host", loadConfig.getCassandraHosts)
    conf.set("spark.cassandra.connection.port", loadConfig.getCassandraPort)
  }

  def updateConfigFromFile(loadConfig: Config): SparkConf = {
    val sparkConf = new SparkConf()
    val entries = loadConfig.entrySet().iterator()
    while (entries.hasNext) {
      val key = entries.next().getKey
      sparkConf.set(key, loadConfig.getString(key))
    }
    sparkConf
  }

}

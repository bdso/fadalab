package function

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.log4j.Logger
import org.apache.spark.SparkConf

object SetConfig {
  val LOG: Logger = Logger.getLogger(this.getClass)

  def setConfig(loadConfig: Settings) = {
    val conf = new SparkConf()
    conf.setAppName(loadConfig.getAppName)
    conf.setMaster(loadConfig.getMaster)
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

  /**
    *
    * @param args
    * @return
    */
  def loadSparkConfig(args: Array[String]): SparkConf = {

    if (args.length < 1) {
      LOG.info("*** Start load config ***")
      LOG.info(
        s"""
           |Usage: FastData <config>
           |  <config> is a config file
           |
            """.stripMargin)
      System.exit(1)
    }

    val loadFileConfig = ConfigFactory.parseFile(new File(args(0)))
    updateConfigFromFile(loadFileConfig)

  }

}

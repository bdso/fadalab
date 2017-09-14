package functiontest

import java.io.File
import java.time.LocalDate
import java.util.Properties

import com.typesafe.config.ConfigFactory
import function.Settings
import kafka.javaapi.producer.Producer
import kafka.producer.{KeyedMessage, ProducerConfig}
import mapping.Object.AppObjOver
import net.liftweb.json.Serialization.write
import net.liftweb.json._
import org.apache.log4j.{Level, Logger}

/**
  * Created by leo on 11/14/16.
  */
object ProducerKafka {

  // bin/kafka-topics.sh --zookeeper kafka:2181 --create --topic bdslab --replication-factor 1 --partitions 1
  // bin/kafka-topics.sh --zookeeper kafka:2181 --list
  // bin/kafka-topics.sh --zookeeper kafka:2181 --describe --topic bdslab
  // bin/kafka-console-producer.sh --broker-list kafka:9092 --topic bdslab
  // bin/kafka-console-consumer.sh --zookeeper kafka:2181 --topic bdslab --from-beginning
  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    if (args.length < 1) {
      System.err.println(
        s"""
           |Usage: ProducerKafka <config>
           |  <config> is a config file
           |
            """.stripMargin)
      System.exit(1)
    }

    val loadConfig = new Settings(ConfigFactory.parseFile(new File(args(0))))
    val props: Properties = new Properties()
    props.put("metadata.broker.list", loadConfig.getBrokers)
    props.put("serializer.class", "kafka.serializer.StringEncoder")

    val kafkaConfig: ProducerConfig = new ProducerConfig(props)
    val producer: Producer[String, String] = new Producer[String, String](kafkaConfig)

    //    val msgObj = new AppObj(LocalDate.now.toString, "ngx-lc", LocalDate.now.toString, "docker_cm_01", "db_cm_01", "10.10.10.14")
    val msgObj = new AppObjOver("2017-09-09T01:59:14.633Z",
      "ngx_lc",
      LocalDate.now.toString,
      "docker-node-4",
      "hd1-ubs_prod",
      "10.10.0.91",
      "-",
      "09/Sep/2017:01:59:14 +0000",
      "200",
      "GET",
      "/health_check",
      "16",
      "\"-\"",
      "\"-\"",
      "\"-\"",
      "\"-\"", 1, 2, 3, 4, 5, 6, 7, 8)

    implicit val formats = DefaultFormats
    val msgJson = write(msgObj)
    while (true) {
      producer.send(new KeyedMessage[String, String](loadConfig.getTopics, msgJson.toString))
      println("Message send: " + msgJson)
      Thread.sleep(5000)
    }
  }
}

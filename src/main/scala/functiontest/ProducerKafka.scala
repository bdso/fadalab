package functiontest

import java.io.File
import java.time.LocalDateTime
import java.util.{Properties, UUID}

import com.typesafe.config.ConfigFactory
import function.Settings
import kafka.javaapi.producer.Producer
import kafka.producer.{KeyedMessage, ProducerConfig}
import mapping.Object.ObjReceive
import net.liftweb.json.Serialization.write
import net.liftweb.json._
import org.apache.log4j.{Level, Logger}
import org.joda.time.DateTime

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

    val msgObj = new ObjReceive(
      UUID.randomUUID().toString(),
      "9921839c-0f0b-4266-ae04-f62c46c7b8c1",
      "9921839c-0f0b-4266-ae04-f62c46c7b8c1",
      LocalDateTime.now().toString,
      "9921839c-0f0b-4266-ae04-f62c46c7b8c1",
      "8.8.8.8",
      "10.20.30.40",
      "192.168.1.10",
      "http://abc.kogi.io",
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/603.3.8 (KHTML, like Gecko) Version/10.1.2 Safari/603.3.8",
      "Viettel Corporation",
      "Vietnam",
      "Ho Chi Minh City",
      "en-us",
      "MacOSX 10.12",
      "Safari",
      "-7",
      "1440px x 900px",
      "Al Bayan, Al Nile",
      "",
      "9921839c-0f0b-4266-ae04-f62c46c7b8c1",
      "9921839c-0f0b-4266-ae04-f62c46c7b8c1",
      "9921839c-0f0b-4266-ae04-f62c46c7b8c1",
      "SDemo",
      "CDemo",
      "MDemo",
      "TDemo",
      "192.168.1.0/24",
      ""
    )

    implicit val formats = DefaultFormats
    val msgJson = write(msgObj)

    while (true) {
      producer.send(new KeyedMessage[String, String](loadConfig.getTopics, msgJson.toString))
      println("Message send: " + msgJson)
      Thread.sleep(5000)
    }
  }
}

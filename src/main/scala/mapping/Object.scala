package mapping

import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

object Object {

  case class ESObj(msg: String)

  case class KafkaStreamObj(topics: String,
                            broker: String,
                            scc: StreamingContext)

  case class ElasticStreamObj(index: String,
                              _type: String,
                              dStream: DStream[AppObj])

  case class ElasticStreamJson(index: String,
                               _type: String,
                               dStream: DStream[String])

  case class AppObj(`@timestamp`: String,
                    `type`: String,
                    timestamp: String,
                    hostsource: String,
                    service: String,
                    remote_addr: String)

}


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
                              dStream: DStream[AppObjOver])

  case class ElasticStreamJson(index: String,
                               _type: String,
                               dStream: DStream[String])

  case class AppObj(`@timestamp`: String,
                    `type`: String,
                    timestamp: String,
                    hostsource: String,
                    service: String,
                    remote_addr: String)

  case class AppObjOver(`@timestamp`: String,
                        `type`: String,
                        timestamp: String,
                        hostsource: String,
                        service: String,
                        remote_addr: String,
                        remote_user: String,
                        time_local: String,
                        status: String,
                        verb: String,
                        request: String,
                        bytes: String,
                        referrer: String,
                        agent: String,
                        xforwardedfor: String,
                        http_authorization: String,
                        a1: Int,
                        a2: Int,
                        a3: Int,
                        a4: Int,
                        a5: Int,
                        a6: Int,
                        a7: Int,
                        a8: Int
                       )

}


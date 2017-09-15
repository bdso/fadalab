
/**
  * Created by leo on 3/31/17.
  */


import java.io.File

import com.typesafe.config.ConfigFactory
import function.FunctionStream._
import function.SetConfig._
import function._
import org.apache.log4j.Logger
import org.apache.spark.SparkConf

object FastData {

  val LOG: Logger = Logger.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {

    val loadFileConfig = ConfigFactory.parseFile(new File(args(0)))
    val loadConfig = new Settings(ConfigFactory.load(loadFileConfig))

    val conf: SparkConf = loadSparkConfig(args)
    application(conf, loadConfig)

  }

}

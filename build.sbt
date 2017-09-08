name := "fastdata"

version := "1.0"

scalaVersion := "2.11.8"

dependencyOverrides ++= {
  val jacksonVersion = "2.8.9"
  Set(
    "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % jacksonVersion
  )
}


libraryDependencies ++= {

  val sparkVersion = "2.1.1"
  val elasticVersion = "5.4.1"
  val typeSafeVersion = "1.3.1"
  val typeSafePlayVersion = "2.6.3"

  Seq(

    /**
      *
      * Spark Streaming.
      *
      */

    //    "org.apache.spark" % "spark-core_2.11" % sparkVersion % "provided",
    //    "org.apache.spark" % "spark-streaming_2.11" % sparkVersion % "provided",
    //    "org.apache.spark" % "spark-sql_2.11" % sparkVersion % "provided",

    //    "org.apache.spark" % "spark-streaming-kafka-0-8_2.11" % sparkVersion % "provided",
    //    "org.elasticsearch" % "elasticsearch-spark-20_2.11" % elasticVersion % "provided",

    "org.apache.spark" % "spark-core_2.11" % sparkVersion,
    "org.apache.spark" % "spark-streaming_2.11" % sparkVersion,
    "org.apache.spark" % "spark-sql_2.11" % sparkVersion,

    "org.apache.spark" % "spark-streaming-kafka-0-8_2.11" % sparkVersion,
    "org.elasticsearch" % "elasticsearch-spark-20_2.11" % elasticVersion,

    /**
      * Load Config files.
      */

    "com.typesafe" % "config" % typeSafeVersion,
    "com.typesafe.play" % "play-json_2.11" % typeSafePlayVersion

    /**
      *
      * Logging.
      *
      */

    //    "org.apache.logging.log4j" % "log4j-api" % "2.8.1",
    //    "org.apache.logging.log4j" % "log4j-core" % "2.8.1",

    /**
      *
      * Testing.
      *
      */

    //    "org.scalactic" %% "scalactic" % "3.0.1",
    //    "org.scalatest" %% "scalatest" % "3.0.1" % "test"

  )
}

assemblyMergeStrategy in assembly := {
  case PathList("org", "aopalliance", xs@_*) => MergeStrategy.last
  case PathList("javax", "inject", xs@_*) => MergeStrategy.last
  case PathList("javax", "servlet", xs@_*) => MergeStrategy.last
  case PathList("javax", "activation", xs@_*) => MergeStrategy.last
  case PathList("org", "apache", xs@_*) => MergeStrategy.last
  case PathList("com", "google", xs@_*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs@_*) => MergeStrategy.last
  case PathList("com", "codahale", xs@_*) => MergeStrategy.last
  case PathList("com", "yammer", xs@_*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

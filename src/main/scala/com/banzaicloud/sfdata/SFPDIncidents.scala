package com.banzaicloud.sfdata

import com.typesafe.scalalogging.Logger
import org.apache.spark.sql.functions._
import org.slf4j.LoggerFactory

object SFPDIncidents extends App {

  case class AppArgs(dataPath: String, awsAccessKeyId: String, awsSecretAccessKey: String, load: String)

  object AppArgs {
    def empty = new AppArgs("", "", "", "")
  }

  override def main(args: Array[String]) {
    val logger = Logger(LoggerFactory.getLogger(this.getClass))

    // WARNING! prints the credentials to the console - remove the line or watch the log level
    logger.debug("Application called with the arguments: [{}]", args.foldLeft("")((x, y) => x + y + " "))

    // parsing application arguments
    val argsInstance = args.sliding(2, 1).toList.foldLeft(AppArgs.empty) {
      case (accumArgs, currArgs) => currArgs match {
        case Array("--awsAccessKeyId", awsAccessKeyId) => accumArgs.copy(awsAccessKeyId = awsAccessKeyId)
        case Array("--awsSecretAccessKey", awsSecretAccessKey) => accumArgs.copy(awsSecretAccessKey = awsSecretAccessKey)
        case Array("--dataPath", dataPath) => accumArgs.copy(dataPath = dataPath)
        case Array("--load", load) => accumArgs.copy(load = load)
        case unknownArg => accumArgs // Do whatever you want for this case
      }
    }

    //   get a reference to the spark configuration
    val spark = org.apache.spark.sql.SparkSession.builder.getOrCreate;

    // set credentials for accessing data in S3
    var hadoopConfig = spark.sparkContext.hadoopConfiguration
    hadoopConfig.set("fs.s3a.access.key", argsInstance.awsAccessKeyId)
    hadoopConfig.set("fs.s3a.secret.key", argsInstance.awsSecretAccessKey)
    hadoopConfig.set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
    hadoopConfig.set("fs.s3a.buffer.dir", "/root/spark/work,/tmp")

    logger.debug("Loading data ...")

    // todo handling the load

    val incidentsDF = spark.read
      .format("csv")
      .option("header", "true") //reading the headers
      .option("mode", "DROPMALFORMED") // drops the malformed lines
      .load(argsInstance.dataPath)

    logger.debug("Data loaded. Row count: {}", incidentsDF.count().toString)

    logger.debug("Start executing SQL operations on the data frame ...")
    var resDF = incidentsDF.select("IncidntNum", "Category", "X", "Y", "Date", "Time")
    resDF.orderBy(desc("Date"))
    resDF.orderBy(desc("Time"))

    logger.debug("Processing of the SFPI data finished")
  }
}

/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package pers.px

import java.util.concurrent.TimeUnit

import org.apache.spark.sql
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.ProcessingTime
import org.apache.spark.sql.types.StructType
import org.slf4j.LoggerFactory

object CSV2Kafka {

  val logger = LoggerFactory.getLogger(getClass)

  /*
  * fields csv文件表头
  * path csv文件路径
  * tableName 将要写入到kafkaMessage中的字段，示例"f_mid_order_details"
  * rowKey 代表rowKey的字段，示例"key"
  * */
  def main(args: Array[String]): Unit = {

    if (args.size != 4) {
      logger.error(s"the number of input params is not match,args is ${args.mkString("<==>")}")
      sys.exit(-1)
    }

    val ss = new sql.SparkSession.Builder().appName(getClass.getSimpleName).getOrCreate
    val Array(fields, path, tableName, rowKey) = args

    var schema = new StructType

    fields.split(",").foreach(field => {
      schema = schema.add(field, "string")
    })

    schema.printTreeString

    val csvDF = ss.readStream
      .schema(schema)
      .option("sep", ",")
      .option("nullValue", "")
      .csv(path)

    csvDF.printSchema

    val etl_time = System.currentTimeMillis / 1000

    val csvDS = csvDF
      .select(to_json(struct(col("*"))).alias("jsonData"))
      .withColumn("opr", lit("INSERT"))
      //      .withColumn("seqNum", lit(s"$etl_time"))
      //      .withColumn("etlTime", col("seqNum"))
      .withColumn("tblName", lit(s"$tableName"))
      .withColumn("rowKey", get_json_object(col("jsonData"), "$." + s"$rowKey"))
      .select(col("rowKey").alias("key"), to_json(struct(col("*"))).alias("value"))
    //      .select(lit("xx").alias("key"), to_json(struct(col("*"))).alias("value"))

    csvDS.printSchema

    csvDS.writeStream
      .format("kafka")
      //      .option("kafka.bootstrap.servers", "172.16.117.101:9092")
      .option("kafka.bootstrap.servers", "hadoop01:9092,hadoop02:9092,hadoop03:9092")
      //      .option("topic", "test-csv")
      //      .option("topic", "jingwei-dm-output")
      .option("topic", "history-data-ll")
      .option("acks", "all")
      .option("checkpointLocation", s"$path/../checkpoint")
      .trigger(ProcessingTime.create(1, TimeUnit.SECONDS)) // only change in query
      .start()

    ss.streams.awaitAnyTermination()
  }
}

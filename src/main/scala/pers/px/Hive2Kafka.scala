/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package pers.px

import org.apache.spark.sql
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{FloatType, LongType}
import org.slf4j.LoggerFactory

object Hive2Kafka {

  val logger = LoggerFactory.getLogger(getClass)

  /*
  * fields csv文件表头
  * path csv文件路径
  * tableName 将要写入到kafkaMessage中的字段，示例"f_mid_order_details"
  * rowKey 代表rowKey的字段，示例"key"
  * */
  def main(args: Array[String]): Unit = {

    if (args.size != 5) {
      logger.error(s"the number of input params is not match,args is ${args.mkString("<==>")}")
      sys.exit(-1)
    }

    System.setProperty("hadoop.home.dir", "C:\\hadoop_test\\Winutils")
    val Array(inSql, tableName, servers, topic, path, _*) = args

    logger.info("args is {}", args.mkString)
    logger.info("inSql is {}", inSql)
    logger.info("tableName is {}", tableName)
    logger.info("servers is {}", servers)
    logger.info("topic is {}", topic)
    logger.info("path is {}", path)

    val ss = new sql.SparkSession.Builder()
      .master("local")
      .appName(getClass.getSimpleName)
      .config("spark.sql.warehouse.dir", "hdfs://hadoopcluster/user/hive/warehouse")
      //      .config("spark.sql.orc.impl", "hive")
      //      .config("spark.sql.orc.enableVectorizedReader", "true")
      .config("fs.defaultFS", "hdfs://hadoopcluster")
      .config("dfs.nameservices", "hadoopcluster")
      .config("dfs.ha.namenodes.hadoopcluster", "nn1,nn2")
      .config("dfs.namenode.rpc-address.hadoopcluster.nn1", "eagle67:9000")
      .config("dfs.namenode.rpc-address.hadoopcluster.nn2", "eagle68:9000")
      .config("dfs.client.failover.proxy.provider.hadoopcluster", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider")
      .enableHiveSupport().getOrCreate

    ss.sql(inSql).select(to_json(struct(col("*"))).alias("jsonData")).show

    ss
      .sql(inSql)
      .select(to_json(struct(col("*"))).alias("jsonData"))
      .withColumn("time",get_json_object(col("jsonData"), "$." + "payment_time"))
      .show


    val hiveDF = ss
      .sql(inSql)
      .select(to_json(struct(col("*"))).alias("jsonData"))
      .withColumn("opr", lit("INSERT"))
      .withColumn("seqNum", lit(current_timestamp().cast(FloatType).*(1000).cast(LongType)))
      .withColumn("etlTime", col("seqNum"))
      .withColumn("rowKey", get_json_object(col("jsonData"), "$." + "key"))
      .withColumn("tblName", lit(s"$tableName"))
      .select(col("rowKey").alias("key"), to_json(struct(col("*"))).alias("value"))

    hiveDF.write
      .format("kafka")
      .option("kafka.bootstrap.servers", s"$servers")
      .option("topic", s"$topic")
      .option("acks", "all")
      .option("timestampFormat", "yyyy-MM-dd HH:mm:ss")
      .option("checkpointLocation", s"$path/checkpoint")
      .save()


    //    hiveDF.show
    //
    //    hiveDF
    //      .select("business_date", "etl_time", "data_deadline_time")
    //      .show
    //
    //    hiveDF.select("business_date", "etl_time", "data_deadline_time")
    //      .select(struct(col("*")))
    //      .show
    //
    //    hiveDF.withColumn("a", get_json_object(get_json_object(col("value"), "$." + "jsonData"), "$." + "business_date"))
    //      .withColumn("b", get_json_object(get_json_object(col("value"), "$." + "jsonData"), "$." + "etl_time"))
    //      .withColumn("c", get_json_object(get_json_object(col("value"), "$." + "jsonData"), "$." + "data_deadline_time"))
    //      .show

  }
}

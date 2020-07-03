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

object TiDB2Kafka1 {
  val logger = LoggerFactory.getLogger(getClass)

  /*
* sqlStr 刷数据执行的sqlStr,示例"SELECT  ent_portal_prod_t_user_info.id AS entPortalTUserInfoId,  ent_portal_prod_t_user_info.mobile AS entPortalTUserInfoMobile,  ent_portal_prod_t_user_info.email AS entPortalTUserInfoEmail,  ent_portal_prod_t_user_info.username AS entPortalTUserInfoUsername,  ent_portal_prod_t_user_info.nickname AS entPortalTUserInfoNickname,  ent_portal_prod_t_user_info.sex AS entPortalTUserInfoSex,  ent_portal_prod_t_user_info.education AS entPortalTUserInfoEducation,  ent_portal_prod_t_user_info.birthday AS entPortalTUserInfoBirthday,  ent_portal_prod_t_user_info.apply_date AS entPortalTUserInfoApplyDate,  ent_portal_prod_t_user_info.province AS entPortalTUserInfoProvince,  ent_portal_prod_t_user_info.city AS entPortalTUserInfoCity,  ent_portal_prod_t_user_info.reg_time AS entPortalTUserInfoRegTime,  ent_portal_prod_t_user_info.add_type AS entPortalTUserInfoAddType,  ent_portal_prod_t_user_info.state AS entPortalTUserInfoState,  ent_portal_prod_t_user_info.create_time_of_user AS entPortalTUserInfoCreateTime FROM  ent_portal_prod_t_user_info limit 20"
* tableName 将要写入到kafkaMessage中的字段，示例"jingwei_version_2_test_table_3"
* rowKey sqlStr中代表rowKey的字段，示例"entPortalTUserInfoId"
* */
  def main(args: Array[String]): Unit = {

    if (args.size != 3) {
      logger.error("the number of input params is not match")
      sys.exit(-1)
    }

    val ss = new sql.SparkSession.Builder().master("local").appName(getClass.getSimpleName).getOrCreate
    val Array(sqlStr, tableName, rowKey) = args

    val jdbcDF = ss.read
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("url", "jdbc:mysql://172.16.116.213:3390/data_warehouse")
      .option("dbtable", s"($sqlStr) t")
      .option("user", "pengxin03")
      .option("password", "pengxin03")
      .load()

    jdbcDF.show

    val jdbcDT = jdbcDF
      .select(to_json(struct(col("*"))).alias("jsonData"))
      .withColumn("opr", lit("INSERT"))
      .withColumn("seqNum", lit(current_timestamp().cast(FloatType).*(1000).cast(LongType)))
      .withColumn("etlTime", col("seqNum"))
      .withColumn("tblName", lit(s"$tableName"))
      .withColumn("rowKey", get_json_object(col("jsonData"), "$." + s"$rowKey"))
      .select(col("rowKey").alias("key"), to_json(struct(col("*"))).alias("value"))

    jdbcDT.show

    jdbcDT.write
      //      .format("com.sunlands.datacenter.ingest.user_defined.sink.KafkaSinkProvider")
      .format("kafka")
      .option("kafka.bootstrap.servers", "172.16.117.73:9092")
      //      .option("topic", "kafka-test")
      .option("topic", "jingwei-dm-output")
      .save()

    //    val messageSchema = StructType(Seq(
    //      StructField("opr", StringType),
    //      StructField("jsonData", StringType),
    //      StructField("seqNum", LongType),
    //      StructField("etlTime", LongType),
    //      StructField("tblName", StringType),
    //      StructField("rowKey", StringType)
    //    ))
    //
    //    val rowEncoder = RowEncoder(messageSchema)
    //
    //    val csvDF = ss.read
    //      .option("header", "true")
    //      .csv("C://Users\\stphen\\Desktop\\t_wide_table.csv")
    //
    //    csvDF.printSchema
    //
    //    val csvDT = csvDF
    //      .select(to_json(struct(col("*"))).alias("jsonData"))
    //      .withColumn("opr", lit("INSERT"))
    //      .withColumn("seqNum", lit(current_timestamp().cast(FloatType).*(1000).cast(LongType)))
    //      .withColumn("etlTime", col("seqNum"))
    //      .withColumn("tblName", lit("test"))
    //      .withColumn("rowKey", get_json_object(col("jsonData"), "$.id"))
    //      .select(col("rowKey").alias("key"), to_json(struct(col("*"))).alias("value"))
    //
    //    csvDT.write
    //      .format("kafka")
    //      .option("kafka.bootstrap.servers", "172.16.117.73:9092")
    //      .option("topic", "kafka-test")
    //      .save()
    //
    //    val tidbOptions: Map[String, String] = Map(
    //      "tidb.addr" -> "172.16.116.213",
    //      "tidb.port" -> "3390",
    //      "tidb.user" -> "root",
    //      "tidb.password" -> "",
    //      "spark.tispark.pd.addresses" -> "172.16.116.213:2379"
    //    )
    //
    //    //    import org.apache.spark.sqlStr.TiContext
    //    //    val ti = new TiContext(ss)
    //    //    ti.tidbMapDatabase("test")
    //    //
    //    //
    //    //
    //    //    ss.sqlStr("select count(*) from t_pv_log limit 10").show(10)
    //    //
    //
    //
    //
    //
    //    //      .withColumn("key", get_json_object(col("value"), "$.id"))
    //    //      .withColumn("opr", lit("INSERT"))
    //    //      .withColumn("jsonData", col("value"))
    //    //      .withColumn("seqNum", current_timestamp())
    //    //      .withColumn("etlTime", current_timestamp())
    //    //      .withColumn("tblName", lit("test"))
    //    //      .withColumn("rowKey", col("key"))
    //
    //
    //    messageSchema.printTreeString()

    //    {
    //      "opr": "INSERT",
    //      "jsonData": "{\"attendDate\":\"2020-04-22\",\"create_time\":\"2020-04-22 12:12:42\",\"device\":\"AndroidApp\",\"eltTime\":0,\"enterTimestamp\":1587528214,\"event_id_str\":\"user_attendance\",\"ip\":\"118.73.44.40\",\"leaveTimestamp\":1587528762,\"liveId\":\"56281\",\"liveProvider\":\"sunlands\",\"location\":\"中国山西临汾\",\"ordDetailId\":6709297,\"roundId\":17323264,\"round_property\":\"SRP_ADDITIONAL\",\"send_time\":\"2020-04-22 12:12:42\",\"system_id\":\"10113\",\"teachUnitId\":5216670,\"type\":\"live\",\"userAgent\":\"\",\"user_identify\":13891776,\"watchDuration\":548}",
    //      "seqNum": 1587528762794,
    //      "etlTime": 1587528762794,
    //      "tblName": "event_log.sunlands_live_data",
    //      "rowKey": "118.73.44.40|56281|13891776|1587528762|6709297|17323264|5216670"
    //    }
  }
}

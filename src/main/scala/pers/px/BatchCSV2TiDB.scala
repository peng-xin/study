package pers.px

import java.sql.{Connection, DriverManager, SQLException}

import org.apache.spark.sql
import org.apache.spark.sql.Encoders
import org.apache.spark.sql.types.StructType
import org.slf4j.LoggerFactory

import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer

object BatchCSV2TiDB {
  val logger = LoggerFactory.getLogger(getClass)
  System.setProperty("hadoop.home.dir", "C:\\hadoop_test\\Winutils")

  def main(args: Array[String]): Unit = {
    val ss = new sql.SparkSession.Builder().master("local").appName(getClass.getSimpleName).getOrCreate

    val Array(fields, tableName, path) = args

    var schema = new StructType

    fields.split(",").foreach(field => {
      schema = schema.add(field, "string")
    })

    val tableInfo = TidbUtils.getFieldAndTypeMap(tableName)

    schema.printTreeString

    val csvDF = ss.read
      .schema(schema)
      .option("sep", ",")
      .csv(path)

    //    csvDF.write
    //      .mode("append")
    //      .format("jdbc")
    //      .options(Map(
    //        "driver" -> "com.mysql.jdbc.Driver",
    //        "url" -> "jdbc:mysql://172.16.116.213:3390/data_warehouse",
    //        "dbtable" -> "f_mid_business_card_tmp_tmp",
    //        "user" -> "tpcc",
    //        "password" -> "tpcc",
    //        "batchsize" -> "1000",
    //        "truncate" -> "false")).save()

    csvDF.foreachPartition(iter => {
      val stateUpdateEncoder = Encoders.product
      val schema = stateUpdateEncoder.schema
      if (iter.nonEmpty) {
        val connection = getConnection()

        var map = new HashMap[String, String]
        val list = new ArrayBuffer[Map[String, String]]
        val dataList = iter.map(internalRow => {
          schema.foreach(field => {
            map += (field.name -> String.valueOf(internalRow.get(schema.indexOf(field))))
          })
          list += map
          if (list.size == 100) {
            TidbUtils.batchIngestDataByMapList(tableName, list.toList)
            list.clear
          }
        })
        if (list.size > 0) {
          TidbUtils.batchIngestDataByMapList(tableName, list.toList)
        }
        connection.close()
      }
    })

  }

  def getConnection(): Connection = {
    try {
      Class.forName("com.mysql.jdbc.Driver")
      val connection = DriverManager.getConnection("jdbc:mysql://172.16.116.213:3390/data_warehouse", "i&u&d", "i&u&d")
      connection.setAutoCommit(false)
      connection
    } catch {
      case e: ClassNotFoundException =>
        logger.error(e.getMessage)
        null
      case throwables: SQLException =>
        logger.error(throwables.getMessage)
        null
    }
  }
}

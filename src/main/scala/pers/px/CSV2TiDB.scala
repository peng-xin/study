package pers.px

import org.apache.spark.sql
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.StructType
import org.slf4j.LoggerFactory

import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer

object CSV2TiDB {
  def main(args: Array[String]): Unit = {
    val logger = LoggerFactory.getLogger(getClass)
    System.setProperty("hadoop.home.dir", "C:\\hadoop_test\\Winutils")

    val Array(fields, tableName, path) = args

    val tableInfo = TidbUtils.getFieldAndTypeMap(tableName)

    val ss = new sql.SparkSession.Builder()
      .master("local")
      .appName(getClass.getSimpleName)
      .getOrCreate


    var schema = new StructType

    fields.split(",").foreach(field => {
      schema = schema.add(field, "string")
    })

    schema.printTreeString

    val DFStream = ss.readStream
      .schema(schema)
      .option("sep", ",")
      .option("header", "false")
      .option("escape", "\"")
      .csv(path)

    val query = DFStream.writeStream.trigger(Trigger.ProcessingTime(1000L))
      .foreachBatch(
        (dataset, batchId) => {
          //          dataset.persist

          dataset.foreachPartition(iter => {
            if (iter.nonEmpty) {
              logger.info(s"batchId is $batchId,")

              var map = new HashMap[String, String]
              val list = new ArrayBuffer[Map[String, String]]

              iter.foreach(internalRow => {
                schema.foreach(field => {
                  map += (field.name -> String.valueOf(internalRow.get(schema.indexOf(field))))
                })
                list += map
                if (list.size == 100) {
                  TidbUtils.batchIngestDataByMapList(tableName, list.toList, tableInfo)
                  list.clear
                }
              })
              if (list.size > 0) {
                TidbUtils.batchIngestDataByMapList(tableName, list.toList, tableInfo)
              }
            }
          })

          logger.info(s"batchId is $batchId,dataset size is ${dataset.count()},partition size is ${dataset.rdd.partitions.size}")
          dataset.show()

          //          dataset.unpersist
        }).start()
    query.awaitTermination()
  }

}

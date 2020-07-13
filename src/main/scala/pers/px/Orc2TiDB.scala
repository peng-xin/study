package pers.px

import org.apache.spark.sql
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer

object Orc2TiDB {
  def main(args: Array[String]): Unit = {
    val logger: Logger = LoggerFactory.getLogger(getClass)
    System.setProperty("hadoop.home.dir", "C:\\hadoop_test\\Winutils")

    val Array(inPath, tableName, _*) = args

    logger.info("args is {}", args.mkString)
    logger.info("inPath is {}", inPath)
    logger.info("tableName is {}", tableName)

    val tableInfo = TidbUtils.getFieldAndTypeMap(tableName)

    logger.info("tableInfo: {}", tableInfo.mkString)

    val ss = new sql.SparkSession.Builder()
      .master("local")
      .appName(getClass.getSimpleName)
      .config("spark.sql.warehouse.dir", "hdfs://hadoopcluster/user/hive/warehouse")
      .config("fs.defaultFS", "hdfs://hadoopcluster")
      .config("dfs.nameservices", "hadoopcluster")
      .config("dfs.ha.namenodes.hadoopcluster", "nn1,nn2")
      .config("dfs.namenode.rpc-address.hadoopcluster.nn1", "eagle67:9000")
      .config("dfs.namenode.rpc-address.hadoopcluster.nn2", "eagle68:9000")
      .config("dfs.client.failover.proxy.provider.hadoopcluster", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider")
      .getOrCreate

    ss
      .read
      .orc(inPath)
      .limit(10)
      .foreachPartition(iter => {
        if (iter.nonEmpty) {
          var map = new HashMap[String, String]
          val list = new ArrayBuffer[Map[String, String]]

          iter.foreach(internalRow => {

            internalRow.schema.foreach(field => {
              map += (field.name -> String.valueOf(internalRow.get(internalRow.schema.indexOf(field))))
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

  }

}

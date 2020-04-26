package pers.px

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object Tidb2Kafka {
  def main(args: Array[String]): Unit = {
//    val conf=new SparkConf
//    conf
//      .setAppName("tidb2kafka")
//      .setMaster("local")
//    val sc=new SparkContext(conf)
//    val ss=new sql.SparkSession.Builder().appName("tidb2kafka").master("local")
    System.setProperty("hadoop.home.dir","D:\\bigdata\\winutils" );
    val sparkConf = new SparkConf().
      setIfMissing("spark.tispark.write.allow_spark_sql", "true").
      setIfMissing("spark.master", "local[*]").
      setIfMissing("spark.app.name", getClass.getName).
      setIfMissing("spark.sql.extensions", "org.apache.spark.sql.TiExtensions").
      setIfMissing("spark.tispark.pd.addresses", "172.16.116.153:2379,172.16.116.206:2379,172.16.116.213:2379,172.16.117.177:2379,172.16.117.77:2379").
      setIfMissing("spark.tispark.tidb.addr", "172.16.116.213").
      setIfMissing("spark.tispark.tidb.port", "4000")

    val spark = SparkSession.builder.config(sparkConf).getOrCreate()

    val sqlContext = spark.sqlContext
//     use TiDB config in the spark config if no data source config is provided
        val tidbOptions: Map[String, String] = Map(
          "tidb.user" -> "root",
          "tidb.password" -> "",
          "database" -> "test"
        )

//        val df = sqlContext.read.
//          format("tidb").
//          options(tidbOptions).
//          option("database", "test").
//          option("table", "t_pv_log").
//          load().
//          limit(10)
//        df.show()

//    spark.sql("show databases").show
    spark.sql("select concat(key,'\"'),* from test.t_pv_log").coalesce(3).write.option("escape","\"").csv("D:\\csv\\sd1")
//    spark.sql("select concat(key,'\"'),* from test.t_pv_log")
//      .write
//      .format("jdbc")
//      .options(tidbOptions)
//      .option("database", "test")
//      .option("table", "t_pv_log")
//      .mode("append")
//      .save()

  }
}

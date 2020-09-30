package pers.px

import java.net.URLDecoder

import org.apache.spark.sql

object NewUser {
  def main(args: Array[String]): Unit = {
    val ss = new sql.SparkSession.Builder()
      .appName(getClass.getName)
      .master("local")
      .getOrCreate

    var start = System.currentTimeMillis

    val csvDF = ss.read
      .option("header", "true")
      .csv("file://"+URLDecoder.decode(getClass.getResource("/访问数据.csv").getPath, "UTF-8"))

    csvDF.createOrReplaceTempView("newUserTable")

    ss.sql("select date,count(*) from (select user,min(date) as date from newUserTable group by user) group by date order by date").show

    println("spark-sql execute cost time:" + (System.currentTimeMillis - start))

  }
}

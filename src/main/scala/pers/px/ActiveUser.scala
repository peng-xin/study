package pers.px

import java.net.URLDecoder

import org.apache.spark.sql
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

object ActiveUser{
  def main(args: Array[String]): Unit = {
    val ss = new sql.SparkSession.Builder()
      .master("local")
      .appName(getClass.getName)
      .getOrCreate

    var start = System.currentTimeMillis

    val csvDF = ss.read
      .option("header", "true")
      .csv(URLDecoder.decode(getClass.getResource("/访问数据.csv").getPath, "UTF-8"))

    val win = Window.partitionBy("user").orderBy("date")
    csvDF.withColumn("rn",row_number() over (win))
      .withColumn("dis", expr("date_sub(date,rn)"))
      .groupBy("user", "dis").agg(min("date"), max("date"), count("user") as "counts")
      .where("counts > 2").show
    
    csvDF.createOrReplaceTempView("activeUserTable")

    ss.sql("select *,rank() OVER (PARTITION BY user ORDER BY date) rank FROM activeUserTable").show
    ss.sql("select *,row_number() OVER (PARTITION BY user ORDER BY date) row_number FROM activeUserTable").show
    ss.sql("select *,dense_rank() OVER (PARTITION BY user ORDER BY date) dense_rank FROM activeUserTable").show
    ss.sql("select *,percent_rank() OVER (PARTITION BY user ORDER BY date) percent_rank FROM activeUserTable").show

    ss.sql("select *,date_sub(date,rank) from (select *,rank() OVER (PARTITION BY user ORDER BY date) rank FROM activeUserTable)").show
    ss.sql("select *,date_sub(date,row_number) from (select *,row_number() OVER (PARTITION BY user ORDER BY date) row_number FROM activeUserTable)").show
    ss.sql("select *,date_sub(date,dense_rank) from (select *,dense_rank() OVER (PARTITION BY user ORDER BY date) dense_rank FROM activeUserTable)").show
    ss.sql("select *,date_sub(date,percent_rank) from (select *,percent_rank() OVER (PARTITION BY user ORDER BY date) percent_rank FROM activeUserTable)").show

    ss.sql("select user , min(date), max(date) ,  count(1)  as counts from (select *,date_sub(date,rank) as dt_start from (select *,rank() OVER (PARTITION BY user ORDER BY date) rank FROM activeUserTable)) group by user,dt_start having counts>2").show
    ss.sql("select user , min(date), max(date) ,  count(1)  as counts from (select *,date_sub(date,row_number) as dt_start from (select *,row_number() OVER (PARTITION BY user ORDER BY date) row_number FROM activeUserTable)) group by user,dt_start having counts>2").show
    ss.sql("select user , min(date), max(date) ,  count(1)  as counts from (select *,date_sub(date,dense_rank) as dt_start from (select *,dense_rank() OVER (PARTITION BY user ORDER BY date) dense_rank FROM activeUserTable)) group by user,dt_start having counts>2").show
    ss.sql("select user , min(date), max(date) ,  count(1)  as counts from (select *,date_sub(date,percent_rank) as dt_start from (select *,percent_rank() OVER (PARTITION BY user ORDER BY date) percent_rank FROM activeUserTable)) group by user,dt_start having counts>2").show


    //    ss.sql("SELECT id,studentId,language,math,english,classId,departmentId FROM (SELECT *, row_number() OVER (PARTITION BY departmentId,classId ORDER BY language+math+english DESC) rank FROM scoresTable ) tmp WHERE rank <= 3").show

    println("spark-sql execute cost time:" + (System.currentTimeMillis - start))
  }
}

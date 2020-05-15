package pers.px

import java.net.URLDecoder

import org.apache.spark.sql
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

object ActiveUser{

  /*
  * rank 与 dense_rank在遇到重复数据时，重复数据产生的序号是相同的
  * rank 如果有重复，后续数据则会产生空洞，如1,1,2,3,4产生的序号为1,1,3,4,5
  * dense_rank 如果有重复，后续数据不会产生空洞，如1,1,2,3,4产生的序号为1,1,2,3,4
  * row_number 则会一直排序，不会对重复数据做特殊处理，如1,1,2,3,4产生的序号为1,2,3,4,5
  *
  * 在存在重复连续数值的数据中，求非重复的连续串长度时，可以使用dense_rank或者row_number，
  * 这两种窗口函数不会因为序列空洞使前后序列产生断链，
  * 示例1,1,2,3,4的最大连续序列在rank中为3，row_number中为4，在dense_rank中为5（1,1重复）
  * 求非重连续序列时，row_number可以一次性求出，dense_rank需要额外去重操作
  *
  * 示例数据中上述现象会出现在序列为11的user中
  * */
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

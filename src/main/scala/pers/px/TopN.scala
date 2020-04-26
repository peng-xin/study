package pers.px

import java.net.URLDecoder

import org.apache.spark.sql

object TopN {
  def main(args: Array[String]): Unit = {
    //数据格式：id,studentId,language,math,english,classId,departmentId，即id，学号，语文，数学，外语，班级，院系

    val ss = new sql.SparkSession.Builder()
      .master("local")
      .appName(getClass.getName)
      .getOrCreate

    var start = System.currentTimeMillis

    /*rdd方式求top*/

    start = System.currentTimeMillis

    val rdd = ss.sparkContext
      .textFile(URLDecoder.decode(getClass.getResource("/分组排序.csv").getPath, "UTF-8"))
      .zipWithIndex
      .filter(_._2 > 0)
      .map(line => line._1.split(","))

    rdd.groupBy(row => row(6) + row(5)) //按院系班级分组
      .map(kv => (kv._1, kv._2.toBuffer.sortWith((a, b) => a.slice(2, 5).map(_.toFloat).sum - b.slice(2, 5).map(_.toFloat).sum > 0).take(3).map(row => row.mkString(",")))) //按成绩排序取前三名
      .sortByKey()
      .flatMap(_._2)
      .foreach(println)

    println("rdd execute cost time:" + (System.currentTimeMillis - start))

    /*spark-sql方式求top*/

    start = System.currentTimeMillis

    val csvDF = ss.read
      .option("header", "true")
      .csv(URLDecoder.decode(getClass.getResource("/分组排序.csv").getPath, "UTF-8"))

    csvDF.createOrReplaceTempView("scoresTable")

    ss.sql("SELECT id,studentId,language,math,english,classId,departmentId FROM (SELECT *, row_number() OVER (PARTITION BY departmentId,classId ORDER BY language+math+english DESC) rank FROM scoresTable ) tmp WHERE rank <= 3").show

    println("spark-sql execute cost time:" + (System.currentTimeMillis - start))

  }
}

package pers.px

import java.net.URLDecoder

import org.apache.spark.sql
import pers.px.NewUser.getClass

object UserRetention {
  def main(args: Array[String]): Unit = {
    val ss = new sql.SparkSession.Builder()
      .appName(getClass.getName)
      .master("local")
      .getOrCreate

    var start = System.currentTimeMillis

    val csvDF = ss.read
      .option("header", "true")
      .csv(URLDecoder.decode(getClass.getResource("/访问数据.csv").getPath, "UTF-8"))

    csvDF.createOrReplaceTempView("userRetentionTable")

    ss.sql("select user,min(date) from userRetentionTable group by user").show
    ss.sql("select userRetentionTable.user,userRetentionTable.date,userStart.start from userRetentionTable left join (select user,min(date) as start from userRetentionTable group by user) userStart on userRetentionTable.user = userStart.user").show(100)
    ss.sql("select userRetentionTable.user as user,userRetentionTable.date as date,userStart.start as start,DATEDIFF(date,start) as byDay from userRetentionTable left join (select user,min(date) as start from userRetentionTable group by user) userStart on userRetentionTable.user = userStart.user order by byDay,user").show(100)
    ss.sql("select start,count(if(byDay = 0, 1, null)) as current,count(if(byDay = 1, 1, null)) as day_1,count(if(byDay = 2, 1, null)) as day_2 from (select userRetentionTable.user as user,userRetentionTable.date as date,userStart.start as start,DATEDIFF(date,start) as byDay from userRetentionTable left join (select user,min(date) as start from userRetentionTable group by user) userStart on userRetentionTable.user = userStart.user order by user) group by start").show(100)
  }
}

package pers.px

import org.apache.spark.{SparkConf, SparkContext}

object WordCountByFile {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("wordcountByFile")
    val sc = new SparkContext(conf)
//    sc.wholeTextFiles("D:\\CentOS7\\rootfs/data/soft/sdkman/candidates/spark/2.4.4/conf/*")
//      .groupByKey //按文件名分组
//      .map(file => (file._1, file._2.flatMap(line => line.split(" "))//切分单词
//        .map((_, 1))//构造(word,1)元组
//        .groupBy(_._1)//按单词进行分组
//        .map(a => (a._1, a._2.unzip._2.sum))//构造wordcount元组
//        .toList
//        .sortWith((a,b)=>a._2 - b ._2 > 0)
//        .take(3))
//      )//取每个文件top3的wordcount
//      .sortBy(_._2.unzip._2.max).collect.foreach(println)

    sc.textFile("D:\\CentOS7\\rootfs/data/soft/sdkman/candidates/spark/2.4.4/conf/*")
      .mapPartitionsWithIndex((i,a)=>{
        val words=a.flatMap(b=>b.split(" "))
//        println("index"+i+"size"+words.mkString)
//        println(words.mkString)
        words
      })
      .collect
      .foreach(println)
    sc.parallelize(1 to 100,2)
      .foreach(println)
    sc.parallelize(1 to 100,2)
        .map(a=>(a%5,a))
      .groupByKey(4)
      .partitions.foreach(a=>println("repartition  "+a.getClass))
    sc.parallelize(1 to 100,2)
      .partitioner.foreach(a=>println("partitioner  "+a.getClass))
    sc.parallelize(1 to 100,2)
      .partitions.foreach(a=>println("partition  "+a.getClass))
  }
}

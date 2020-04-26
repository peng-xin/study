package pers.px

import org.apache.spark.{SparkConf, SparkContext}

object test {
  def main(args: Array[String]): Unit = {
    val traversable = Traversable(1 to 10: _*)
    val iterable = Iterable(1 to 10: _*)
  }
}

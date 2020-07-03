package pers.px

object MaxSubArray {
  def main(args: Array[String]): Unit = {

  }

  def maxSubArray(nums: Array[Int]): Int = {
    nums.tail.foldLeft((nums.head,nums.head)) { case ((preMax,preDp), cur) =>
      (cur + preDp).max(cur).max(preMax) -> (cur + preDp).max(cur)
    }._1

    var max = Int.MinValue
    var sum = 0
    nums.foreach(a => {
      sum += a
      if (sum > max) {
        max = sum
      }
      if (sum < 0) {
        sum = 0
      }
    })
    max
  }
}

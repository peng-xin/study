package pers.px

object Kmps {
  def main(args: Array[String]): Unit = {
    println(makeNext("ABCDABCD").mkString)
    println(makeNext("ABCDABD").mkString)
    println(makeNext("abab").mkString)

    println(kmp("ABCDACDBDBCDAB", "DBDBA"))
  }

  def kmp(text: String, pattern: String): Int = {
    var text_index = 0
    var pattern_index = 0
    val next: Array[Int] = makeNext(pattern)
    while (text_index < text.size && pattern_index < pattern.size) {
      if (pattern_index == -1 || text(text_index) == pattern(pattern_index)) {
        text_index += 1
        pattern_index += 1
      } else {
        pattern_index = next(pattern_index)
      }
    }
    if (pattern_index == pattern.size) {
      text_index - pattern_index
    } else {
      -1
    }
  }

  def makeNext(pattern: String): Array[Int] = {
    var next = -1
    var index = 0
    var result = new Array[Int](pattern.size)
    result(index) = next
    while (index < pattern.size - 1) {
      println(index + "=>" + next)
      if (next == -1 || pattern(index) == pattern(next)) {
        next += 1
        index += 1
                if(pattern(index) != pattern(next)){
        result(index) = next
                }else{
                  result(index)=result(next)
                }
      } else {
        next = result(next)
      }
    }
    println(result.mkString)
    result
  }
}

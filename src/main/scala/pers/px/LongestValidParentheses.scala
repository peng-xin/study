package pers.px

import scala.collection.mutable


object LongestValidParentheses {
  def main(args: Array[String]): Unit = {
    //    println(longestValidParentheses("(()"))
    println(longestValidParenthesesStack(")))()())"))
    println(longestValidParenthesesStack(")()())"))
    println(longestValidParenthesesStack("()"))
    println(longestValidParenthesesStack("()()"))

    println(longestValidParenthesesDp(")()())"))
    println(longestValidParenthesesDp("()"))
    println(longestValidParenthesesDp("()()"))
    println(longestValidParenthesesDp("(()())"))
    println(longestValidParenthesesDp("()(()())"))
    println(longestValidParenthesesDp("()((()())"))
    println(longestValidParenthesesDp("()((()()))"))

  }

  def longestValidParenthesesStack(s: String): Int = {
    var max = 0
    val stack = new mutable.Stack[Int]
    stack.push(-1)
    for (i <- 0 until s.size) {
      if (s(i) == '(') {
        stack.push(i)
      } else {
        stack.pop()
        if(stack.nonEmpty){
          max=math.max(max,i-stack.top)
        }else{
          stack.push(i)
        }
      }
    }
    max
  }

  def longestValidParenthesesDp(s: String): Int = {
    val dp = new Array[Int](s.size)
    var max = 0

    for (i <- 1 until s.size) {
      if (s(i) == ')') {
        if (s(i - 1) == '(') {
          dp(i) = (if (i - 2 >= 0) dp(i - 2) else 0) + 2
        } else if ((i - dp(i - 1)) > 0 && s(i - dp(i - 1) - 1) == '(') {
          dp(i) = dp(i - 1) + (if (i - dp(i - 1) - 2 >= 0) dp(i - dp(i - 1) - 2) else 0) + 2
        }
        max = math.max(max, dp(i))
      }
    }
    max
  }
}

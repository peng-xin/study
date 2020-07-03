package pers.px

object ReverseList {

  def main(args: Array[String]): Unit = {

  }

  def reverseList(head: ListNode): ListNode = {
    var cur = head
    var next: ListNode = null
    var pre: ListNode = null
    while (cur != null) {
      next = cur.next
      cur.next = pre
      pre = cur
      cur = next
    }
    pre
  }

  class ListNode(var _x: Int = 0) {
    var next: ListNode = null
    var x: Int = _x
  }
}



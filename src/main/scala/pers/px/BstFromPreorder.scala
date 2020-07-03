package pers.px

import scala.collection.mutable

object BstFromPreorder {

  def main(args: Array[String]): Unit = {
    bfs(bstFromPreorder(Array(8, 5, 1, 7, 10, 12)))
  }

  def bstFromPreorder(preorder: Array[Int]): TreeNode = {
    val root = new TreeNode(preorder(0))
    var tmp = root
    var insertFlag = false
    preorder.tail.foreach(value => {
      while (!insertFlag) {
        if (tmp.value > value) {
          if (tmp.left != null) {
            tmp = tmp.left
          } else {
            tmp.left = new TreeNode(value)
            insertFlag = !insertFlag
          }
        } else {
          if (tmp.right != null) {
            tmp = tmp.right
          } else {
            tmp.right = new TreeNode(value)
            insertFlag = !insertFlag
          }
        }
      }
      tmp = root
      insertFlag = false
    })
    root
  }

  def bfs(root: TreeNode): Unit = {
    val queue = new mutable.Queue[TreeNode]
    queue.enqueue(root)
    while (queue.nonEmpty) {
      val size = queue.size
      var index = 0
      while (index < size) {
        val tmp = queue.dequeue()
        print(" " + tmp.value + " ")
        if (tmp.left != null) {
          queue.enqueue(tmp.left)
        }
        if (tmp.right != null) {
          queue.enqueue(tmp.right)
        }
        index += 1
      }
      println()
    }
  }

  class TreeNode(_value: Int = 0, _left: TreeNode = null, _right: TreeNode = null) {
    var value: Int = _value
    var left: TreeNode = _left
    var right: TreeNode = _right
  }

}


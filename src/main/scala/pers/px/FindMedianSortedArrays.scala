package pers.px

object FindMedianSortedArrays {
  def main(args: Array[String]): Unit = {
    println(findMedianSortedArrays(Array(1, 2, 3), Array(4, 5, 6, 7)))
    println(findMedianSortedArrays(Array(1, 2, 3), Array(4, 5, 6, 7, 8)))
    println(findMedianSortedArrays(Array(4, 5, 6, 7), Array(1, 2, 3)))
    println(findMedianSortedArrays(Array(4, 5, 6, 7, 8), Array(1, 2, 3)))

  }

  def findMedianSortedArrays(nums1: Array[Int], nums2: Array[Int]): Double = {
    val size1 = nums1.size
    val size2 = nums2.size
    if (nums1(size1 - 1) <= nums2(0)) {
      if (size1 < size2) {
        if ((size1 + size2) % 2 != 0) {
          nums2((size1 + size2) / 2 - size1)
        } else {
          (nums2((size1 + size2) / 2 - size1 - 1) + nums2((size1 + size2) / 2 - size1)) / 2.0
        }
      } else if (size2 < size1) {
        if ((size1 + size2) % 2 != 0) {
          nums1((size1 + size2) / 2)
        } else {
          (nums1((size1 + size2) / 2 - 1) + nums1((size1 + size2) / 2)) / 2.0
        }
      } else {
        (nums1(size1 - 1) + nums2(0)) / 2.0
      }
    } else if (nums2(size2 - 1) <= nums1(0)) {
      findMedianSortedArrays(nums2, nums1)
    } else if (nums2(0) < nums1(size1 - 1) && nums1(size1 - 1) < nums2(size2 - 1)) {
      if ((size1 + size2) % 2 != 0) {
1
      } else {
        (nums1((size1 + size2) / 2 - 1) + nums1((size1 + size2) / 2)) / 2.0
      }
    } else {
      findMedianSortedArrays(nums2, nums1)
    }
  }
}

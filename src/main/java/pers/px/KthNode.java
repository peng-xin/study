/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package pers.px;

import java.util.Stack;

public class KthNode {
    public class TreeNode {
        int val = 0;
        TreeNode left = null;
        TreeNode right = null;

        public TreeNode(int val) {
            this.val = val;

        }

    }

    public class Solution {
        TreeNode KthNode(TreeNode pRoot, int k) {
            Stack<TreeNode> stack = new Stack<>();
            TreeNode treeNode = pRoot;
            int index = 0;
            while (!stack.empty() || treeNode != null) {
                while (treeNode != null) {
                    stack.push(treeNode);
                    treeNode = treeNode.left;
                }
                treeNode = stack.pop();
                System.out.println("index " + index + " value is " + treeNode.val);
                if (index++ == k) {
                    return treeNode;
                }
                treeNode = treeNode.right;
            }
            return null;
        }
    }
}

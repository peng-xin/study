/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package pers.px;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

public class RKthNode {
    public class TreeNode {
        int val = 0;
        TreeNode left = null;
        TreeNode right = null;

        public TreeNode(int val) {
            this.val = val;

        }

    }

    public static void main(String[] args) {
        RKthNode rKthNode=new RKthNode();
        TreeNode root=rKthNode.buildBST(new int[]{5,3,7,2,4,6,8});
        rKthNode.bfs(root);
        new RKthNode.Solution().RKthNode(root,5);
    }

    public static class Solution {
        TreeNode RKthNode(TreeNode pRoot, int k) {
            Stack<TreeNode> stack = new Stack<>();
            TreeNode treeNode = pRoot;
            int index = 1;
            while (!stack.empty() || treeNode != null) {
                while (treeNode != null) {
                    stack.push(treeNode);
                    treeNode = treeNode.right;
                }
                treeNode = stack.pop();
                System.out.println("index " + index + " value is " + treeNode.val);
                if (index++ == k) {
                    return treeNode;
                }
                treeNode = treeNode.left;
            }
            return null;
        }
    }

    public TreeNode buildBST(int[] vArray) {
        TreeNode root = new TreeNode(vArray[0]);
        for (int i = 1; i < vArray.length; i++) {
            insert(root, vArray[i]);
        }
        return root;
    }

    public void insert(TreeNode treeNode, int value) {
        TreeNode node=treeNode;
        while (node != null) {
            if (node.val > value) {
                if (node.left == null) {
                    node.left = new TreeNode(value);
                    break;
                } else {
                    node = node.left;
                }
            } else {
                if (node.right == null) {
                    node.right = new TreeNode(value);
                    break;
                } else {
                    node = node.right;
                }
            }
        }
    }

    public void bfs(TreeNode root) {
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        TreeNode treeNode = root;
        while (!queue.isEmpty()) {
            for (int size = queue.size(); size > 0; size--) {
                treeNode=queue.poll();
                System.out.print(treeNode.val+" ");
                if(treeNode.left!=null){
                    queue.add(treeNode.left);
                }
                if(treeNode.right!=null){
                    queue.add(treeNode.right);
                }
            }
            System.out.println();
        }
    }
}

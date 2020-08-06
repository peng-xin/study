package pers.px;

import java.util.ArrayDeque;
import java.util.Queue;

public class IsLegalHeap {
    public boolean isLegalHeap(TreeNode root) {
        if (root == null) {
            return true;
        }
        return checkHeap(root, true) || checkHeap(root, false);
    }

    private boolean checkHeap(TreeNode root, boolean bigHeapFlag) {
        boolean heapFlag = true;
        boolean hasNullChildFlag = false;
        Queue<TreeNode> treeNodes = new ArrayDeque<>();
        TreeNode treeNode;
        treeNodes.add(root);
        while (!treeNodes.isEmpty()) {
            treeNode = treeNodes.poll();
            System.out.println(treeNode);
            if (treeNode.left == null && treeNode.right != null) {
                heapFlag = false;
                break;
            }

            if (hasNullChildFlag && (treeNode.left != null || treeNode.right != null)) {
                heapFlag = false;
                break;
            }

            if (treeNode.left != null) {
                if (bigHeapFlag ^ treeNode.val < treeNode.left.val) {
                    heapFlag = false;
                    break;
                }
                treeNodes.add(treeNode.left);
            } else {
                hasNullChildFlag = true;
            }

            if (treeNode.right != null) {
                if (bigHeapFlag ^ treeNode.val < treeNode.right.val) {
                    heapFlag = false;
                    break;
                }
                treeNodes.add(treeNode.right);
            } else {
                hasNullChildFlag = true;
            }
        }
        return heapFlag;
    }

}


class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

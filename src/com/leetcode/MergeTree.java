package com.leetcode;

public class MergeTree {
	
	 
	class Solution {
	    public TreeNode mergeTrees(TreeNode t1, TreeNode t2) {
	    	if (t1 == null) {
	    		return t2;
	    	}
	    	if (t2 == null) {
	    		return t1;
	    	}
	    	TreeNode root = new TreeNode(t1.val + t2.val);
	    	// 合并它的左子树
	    	root.left = mergeTrees(t1.left, t2.left);
	    	// 合并它的右子树
	    	root.right = mergeTrees(t1.right, t2.right);
	    	return root;

	    }
	}

}

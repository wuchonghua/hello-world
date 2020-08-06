package com.leetcode;

import java.util.ArrayList;
import java.util.List;

public class ConvertBstToGreaterTree {
	 
	class Solution {
	    public TreeNode convertBST(TreeNode root) {
	    	if (root == null) {
	            return null;
	        }
	    	List<TreeNode> l = func(root);
	    	for (int i = 1; i < l.size(); i++) {
	    		l.get(i).val += l.get(i - 1).val;
	    	}
	    	return root;
	    }
	    
	    public List<TreeNode> func(TreeNode root) {
	    	List<TreeNode> l = new ArrayList<>();
	    	if (root.right != null) {
	    		l.addAll(func(root.right));
	    	}
	    	l.add(root);
	    	if (root.left != null) {
	    		l.addAll(func(root.left));
	    	}
	    	return l;
	    }
	}
}

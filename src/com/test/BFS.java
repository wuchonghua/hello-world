package com.test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class BFS {
	
	 //Definition for a binary tree node.
	 class TreeNode {
	     int val;
	     TreeNode left;
	     TreeNode right;
	     TreeNode(int x) { val = x; }
	 }
	 
	    public List<List<Integer>> levelOrder(TreeNode root) {
	    	if (root != null) {
	    		Queue<List<TreeNode>> queue = new ArrayDeque<>(); 
		        List<List<Integer>> result = new ArrayList<>();
		        List<TreeNode> rootL = new ArrayList<>();
		        rootL.add(root);
		         queue.add(rootL);
		        List<Integer> l = new ArrayList<Integer>();
		        l.add(root.val);
		        result.add(l);
		        while(queue.peek() != null) {
		        	List<TreeNode> ts = queue.poll();
		        	List<Integer> l2 = new ArrayList<Integer>();
		        	List<TreeNode> ts2 = new ArrayList<>();
		        	for (TreeNode t : ts) {
		        		if (t.left != null) {
		        			l2.add(t.left.val);
		        			ts2.add(t.left);
		        		}
		        		if (t.right != null) {
		        			l2.add(t.right.val);
		        			ts2.add(t.right);
		        		}
					}
		        	if (ts2.size() > 0) {
		        		result.add(l2);
		        		queue.add(ts2);
		        	}
		        }
		        return result;
	    	} else {
	    		return null;
	    	}
	        
	    }
	

}

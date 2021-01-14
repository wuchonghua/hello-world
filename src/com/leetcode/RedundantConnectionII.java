package com.leetcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedundantConnectionII {

	public static void main(String[] args) {
		int[][] edges = {{2,1},{3,1},{4,2},{1,4}};
		int[]  result = findRedundantDirectedConnection(edges);
		for (int i : result) {
			System.out.println(i);
		}
	}
	
    public static int[] findRedundantDirectedConnection(int[][] edges) {
    	
    	int[] parent = new int[edges.length + 1];
    	for (int i = 0; i < edges.length; i++) {
    		parent[edges[i][1]] = edges[i][0];
		}
    	
    	
    	
    	
        Set<Integer> s = new HashSet<>(edges.length);
        Map<Integer, Integer> m = new HashMap<>();
        int[] result = edges[0];
        // 看这个节点的父亲 是不是只有一个
        // 不妨假设edge[0][0]是根节点
        int root = edges[0][0];
        for (int i = 0; i < edges.length; i++) {
            if (edges[i][1] == root) {
                if (s.contains(edges[i][0])) {
                    result = edges[i];
                } else {
                    root = edges[i][0];
                }
            } else {
                if (!m.containsKey(edges[i][1])) {
                    m.put(edges[i][1], edges[i][0]);
                } else {
                    result = edges[i];
                }
            }
        }
        return result;
    }
}

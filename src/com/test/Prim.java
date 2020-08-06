package com.test;

import java.util.ArrayList;
import java.util.List;

public class Prim {
	
	private static int[][] G = {{0, 6, 1, 5, 0, 0},
						 		{6, 0, 5, 0, 3, 0},
					 			{1, 5, 0, 5, 6, 4},
					 			{5, 0, 5, 0, 0, 2},
					 			{0, 3, 6, 0, 0, 6},
					 			{0, 0, 4, 2, 6, 0}};
	
	public static void main(String[] args) {
		List<Integer> mst = new ArrayList<>();
		
		
		List<Integer> visited = new ArrayList<Integer>();
		for(int i = 0; i < G[0].length; i++) {
			
		}
	}

}

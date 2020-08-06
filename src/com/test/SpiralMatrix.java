package com.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpiralMatrix {

	public static void main(String[] args) {
		int[][] matrix = {{6,9,7}};
		List<Integer> l = spiralOrder(matrix);
		for (Integer integer : l) {
			System.out.println(integer);
		}
	}

	private static List<Integer> spiralOrder(int[][] matrix) {

		List<Integer> l = new ArrayList<Integer>();
		// 重叠子问题
		while (matrix.length > 0 && matrix[0] != null) {

			for (int i = 0; i < matrix.length; i++) {
				if (i == 0) {
					for (int j = 0; j < matrix[i].length; j++) {
						l.add(matrix[i][j]);
					}
				} else if (i == matrix.length - 1) {
					for (int j = matrix[i].length - 1; j >= 0; j--) {
						l.add(matrix[i][j]);
					}
				} else {
					l.add(matrix[i][matrix[i].length - 1]);
				}
			}
			
			for (int i = matrix.length - 2; i > 0; i--) {
				// 防止第二维只有一个数据会添加两次的情况
				if (matrix[i].length > 1) {
					l.add(matrix[i][0]);
				}
			}
			if (matrix.length > 2) {
				int[][] matrix1 = new int[matrix.length - 2][];
				for (int i = 1; i < matrix.length - 1; i++) {
					if (matrix[i].length > 2) {
						matrix1[i - 1] = Arrays.copyOfRange(matrix[i], 1, matrix[i].length - 1);
					}
				}
				matrix = matrix1;
			} else {
				for (int i = 1; i < matrix[0].length - 1; i++) {
					if (matrix.length > 2) {
						l.add(matrix[0][i]);
					}
				}
				break;
			}
		}
		return l;
	}

}

package com.test;

public class Hanoi {
	
	public static void main(String[] args) {
		hanoi(3,'x','y','z');
	}

	public static void hanoi(int n, char x, char y, char z) {
		if(n == 1) {
			move(x, 1 , z);
		}
		else {
			hanoi(n-1, x, z, y);
			move(x, n , z);
			hanoi(n-1, y, x, z);
		}
	}

	public static void move(char x, int n, char z) {
		System.out.println("将"+n+"号盘从"+x+"移到"+z);
	}
	
	public String longestCommonPrefix(String[] strs) {
		if (strs == null || strs.length == 0) {
			return "";
		}
		if(strs.length == 1) {
			return strs[0];
		}
		StringBuilder s = new StringBuilder();
		boolean flag = false;
		for (int j = 0;; j++) {
			for (int i = 0; i < strs.length - 1; i++) {
				try {
					if (strs[i].charAt(j) != strs[i + 1].charAt(j)) {
						flag = true;
						break;
					}
				} catch (Exception e) {
					flag = true;
					break;
				}

			}
			if (flag) {
				break;
			} else {
				s.append(strs[0].charAt(j));
			}
		}

		return s.toString();
	}
}

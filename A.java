package com.xhdl.it.xhms.device.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class A {
	private static Map<Integer,Character> m = new HashMap<>();
	
	static {
		int j = 1;
		for(char i = 'a'; i<='z'; i++) {
			m.put(j , i);
			j++;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(convert(33));
	}
	
	public static String convert(int num) {
		// 先转10进制
		int num10 = 0;
		int mi = 0;
		while(num != 0) {
			num10 += num%10 * Math.pow(8, mi);
			mi++;
			num /= 10;
		}
		System.out.println(num10);
		// 10进制转26进制
		Stack<String> s = new Stack<String>();
		while(num10 != 0) {
			s.push(num10%26 == 0? "0" : String.valueOf(m.get(num10%26)));
			num10 /= 26;
		} 
		String result = "";
		while (!s.empty()) {
			result = result + s.pop();
		}
		return result;
	}

}

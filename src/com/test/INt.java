package com.test;

import java.util.HashMap;
import java.util.Map;

public class INt {
	public static void main(String[] args) {
		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("2", 1);
		int i = m.get("3");
		System.out.println(i);
	}

}

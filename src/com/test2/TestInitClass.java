package com.test2;

import java.util.Arrays;

public class TestInitClass {
	
	public TestInitClass() {
		System.out.println("----------");
	}
	static class Test2 {
		static {
			System.out.println("test2");
		}
	}

}

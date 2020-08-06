package com.test;

public class TestClassLoader {
	
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		Class clazz = TestClassLoader.class.getClassLoader().loadClass("com.test.Hanoi");
		
		System.out.println("-----");
		
		clazz.getMethods();
	}

}

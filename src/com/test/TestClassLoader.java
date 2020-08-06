package com.test;

public class TestClassLoader {
	
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		Class clazz = Test.class.getClassLoader().loadClass("com.test.T1");
		
		System.out.println("-----");
		
		clazz.getMethods();
	}

}

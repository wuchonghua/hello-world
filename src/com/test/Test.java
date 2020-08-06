package com.test;

public class Test {
	
	public static void main(String[] args) {
		System.out.println(ProxyFactory.createProxy(XX.class).toString());
	}
	
	class A{
		public void test1() {
			System.out.println();
		}
	}

}

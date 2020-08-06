package com.test;

import java.util.HashMap;
import java.util.Map;

public class TestVolatile2 {
	
    class InnerA{
		int i;
	}
	
    public static void main(String[] args) {
    	TestVolatile2 a1 = new TestVolatile2();
    	InnerA a = a1.new InnerA();
    }
	
//	
//	private static InnerA a = new InnerA();
//	private static volatile int[] is = new int[10];
//	private static int num;
//	private static boolean flag = true;
//	
//	static class ReaderThread extends Thread{
//		@Override
//		public void run() {
////			while(flag) {
////				
////			}
////			while(is[5] != 1) {
////				
////			}
//			while(a.i != 1) {
//				
//			}
//			System.out.println(num);
//		}
//	}
//	public static void main(String[] args) throws InterruptedException {
//		new ReaderThread().start();
//		Thread.sleep(1000);
//		num = 12;
////		flag = false;
////		is[5] = 1;
//		a.i = 1;
//	}
	

}

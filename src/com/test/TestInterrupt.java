package com.test;

public class TestInterrupt {
	
	
	public static void main(String[] args) throws InterruptedException {
		T1 t1 = new T1();
		t1.start();
		Thread.sleep(1000);
		t1.interrupt();
		
		
	}
	
	private static class T1 extends Thread {
		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					return;
				}
				System.out.println(123);
			}
		}
	}

}

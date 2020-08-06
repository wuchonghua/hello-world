package com.test2;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class TestCountDownLatch2 {
	
	
	public static void main(String[] args) throws InterruptedException {
		CountDownLatch cdl = new CountDownLatch(2);
		for(int i = 0; i < 1; i++) {
			new Thread(() -> {
				cdl.countDown();
				try {
					cdl.await();
				}catch(Exception e) {
					
				}
				System.out.println("我是" + Thread.currentThread().getName() + ",正在运行");
				
			}).start();
		}
		Thread.sleep(5000);
		cdl.countDown();
		cdl.await();
		//ThreadLocal<T>
		//ConcurrentHashMap<K, V>
		System.out.println("我是" + Thread.currentThread().getName() + ",等待中..");
		int i = 0;
		System.out.println(i++ == 0);
		System.out.println("我是" + Thread.currentThread().getName() + ",正在运行");
	}

}

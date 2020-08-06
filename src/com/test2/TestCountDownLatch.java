package com.test2;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class TestCountDownLatch {
	
	public static void main(String[] args) throws InterruptedException {
		CountDownLatch cdl = new CountDownLatch(2);
		for(int i = 0; i < 3; i++) {
			new Thread(() -> {
				System.out.println("我是" + Thread.currentThread().getName() + ",正在运行");
//				try {
//					Thread.sleep(Thread.activeCount() * 2000);
//				} catch (InterruptedException e) {
//				}
				cdl.countDown();
			}).start();
		}
		Thread.sleep(1000);
		System.out.println("我是" + Thread.currentThread().getName() + ",等待中..");
		cdl.await();
		System.out.println("我是" + Thread.currentThread().getName() + ",正在运行");
	}

}

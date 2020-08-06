package com.test2;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class X {
	public static void main(String[] args) throws Exception {
//		System.out.println("adafsaf");
//		
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		A.get();
//		ClassLoader c = X.class.getClassLoader();
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Class clazz = c.loadClass("com.test.ADA");
//		clazz.getMethod("getResult");
		ReentrantLock s = new ReentrantLock();
		s.lock();
		s.lock();
		System.out.println(11111);
		s.lock();
		System.out.println(1111);
	}

}

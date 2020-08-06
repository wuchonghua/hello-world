package com.test2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class TestReentrantLock {
	
	private static TestInitClass t = new TestInitClass();
	
	public static void get() {
		ReentrantLock lock = new ReentrantLock();
		lock.unlock();
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		List<String> list = new ArrayList<>();      
		Map<String, String> m = new HashMap<>();    
		list.add("1");      
		list.add("2"); 
		m.put("1", "123");
		m.put("2", "123");
		int c = 214_748_3647;
		int nextc = c + 1;
		System.out.println(nextc);
		Class.forName("sun.misc.Unsafe");
//		for (String item : list) {
//			if ("1".equals(item)) {
//				list.remove(item);
//			}
//		}
//		for (Map.Entry set : m.entrySet()) {
//			if (set.getKey() == "1") {
//				m.remove("1");
//			}
//		}
		
//		
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		TestReentrantLock.get();
//		ClassLoader c = TestReentrantLock.class.getClassLoader();
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Class clazz = c.loadClass("com.test.TestVolatile");
//		clazz.getMethod("getResult");
		ReentrantLock s = new ReentrantLock();
		s.lock();
		s.lock();
		System.out.println(11111);
		s.lock();
		System.out.println(1111);

	}

}

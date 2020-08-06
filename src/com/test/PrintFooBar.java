package com.test;

import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

public class PrintFooBar {
	
	public static void main(String[] args) throws InterruptedException {
		PrintFooBar a = new PrintFooBar();
		FooBar f = a.new FooBar(2);
		PrintBar pb = a.new PrintBar();
		PrintFoo pf = a.new PrintFoo();
		f.foo(pf);
		f.foo(pb);
	}

	class FooBar {
	    private int n;
	    
	    private Object lock = new Object();
	    private volatile boolean canFoo = true;
	    private volatile boolean canBar;

	    public FooBar(int n) {
	        this.n = n;
	    }

	    public void foo(Runnable printFoo) throws InterruptedException {
	        
	        for (int i = 0; i < n; i++) {
	            synchronized(lock){
	                while(!canFoo){
	                    lock.wait();
	                }
	                // printFoo.run() outputs "foo". Do not change or remove this line.
	        	    printFoo.run();
	                canBar = true;
	                canFoo = false;
	                lock.notifyAll();
	            }
	        	
	        }
	    }

	    public void bar(Runnable printBar) throws InterruptedException {
	        
	        for (int i = 0; i < n; i++) {
	            synchronized(lock){
	                while(!canBar){
	                    lock.wait();
	                }
	                // printBar.run() outputs "bar". Do not change or remove this line.
	        	    printBar.run();
	                canFoo = true;
	                canBar = false;
	                lock.notifyAll();
	            }
	            
	        }
	    }
	}
	
	
	class PrintBar implements Runnable {

		@Override
		public void run() {
			System.out.print("bar");
		}
		
	}
	
	class PrintFoo implements Runnable {

		@Override
		public void run() {
			System.out.print("foo");
		}
		
	}
	
}

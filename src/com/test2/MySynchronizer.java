package com.test2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class MySynchronizer {
	
	private static class Sync extends AbstractQueuedSynchronizer {
		
		Sync() {
			setState(1);
		}
		
		@Override
		protected boolean tryRelease(int arg) {
			int c = getState();
			compareAndSetState(c, c - 1);
			return c - 1 == 0;
		}
		
		@Override
		protected boolean tryAcquire(int arg) {
			return getState() == 0;
		}
	}
	
	private static final class Sync2 extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        private final int count;
        
        Sync2(int count) {
        	this.count = count;
            setState(count);
        }

        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (;;) {
                int c = getState();
                System.out.println(Thread.currentThread() + "---------" + c);
                int nextc = c-1;
                if (c == 0)
                	nextc = count - 1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }
	
	public static void main(String[] args) throws Exception {
//		Sync m = new Sync();
//		new Thread(() -> {
//			m.acquire(1);
//			/*
//			 * try { Thread.sleep(1000); } catch (InterruptedException e) { // TODO
//			 * Auto-generated catch block e.printStackTrace(); }
//			 */System.out.println(1123213);}) .start();
//		Thread.sleep(2000);
//		m.release(1);

		// System.out.println(1123213);
		
		Sync2 cdl = new Sync2(2);
		//CountDownLatch cdl = new CountDownLatch(2);
		for(int i = 0; i < 4; i++) {
			new Thread(() -> {
				System.out.println("我是" + Thread.currentThread().getName() + ",正在运行");
				try {
					Thread.sleep(Thread.activeCount() * 2000);
				} catch (InterruptedException e) {
				}
				cdl.releaseShared(1);
				//cdl.countDown();
			}).start();
		}
		System.out.println("我是" + Thread.currentThread().getName() + ",等待中..");
		cdl.acquireSharedInterruptibly(1);
		//cdl.await();
		System.out.println("我是" + Thread.currentThread().getName() + ",正在运行");
		
		for(int i = 0; i < 4; i++) {
			new Thread(() -> {
				System.out.println("我是" + Thread.currentThread().getName() + ",正在运行");
//				try {
//					Thread.sleep(Thread.activeCount() * 2000);
//				} catch (InterruptedException e) {
//				}
				cdl.releaseShared(1);
			}).start();
		}
		Thread.sleep(500);
		System.out.println("我是" + Thread.currentThread().getName() + ",等待中..");
		cdl.acquireSharedInterruptibly(1);
		System.out.println("我是" + Thread.currentThread().getName() + ",正在运行");
		
	}

}

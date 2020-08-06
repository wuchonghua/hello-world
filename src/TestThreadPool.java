import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestThreadPool {
	
	public static void main(String[] args) throws InterruptedException {
		
		for (int i = 1; i <= 8; i++) {
			threadPool.execute(new MyThread(i));
		}
		
	}
	
	//Executors
	
	static ExecutorService threadPool = new ThreadPoolExecutor(2, 3, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(2), new ThreadPoolExecutor.AbortPolicy());
	
	static class MyThread implements Runnable {
		String name;
		int i;
		public MyThread(int i) {
			name = "name" + i;
			this.i = i;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(i * 100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + "执行");
		}
		
	}

}

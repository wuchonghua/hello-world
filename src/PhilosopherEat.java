import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class DiningPhilosophers {
	
	Object[] os;

    public DiningPhilosophers() {
    	os = new Object[5];
    	for (int i = 0; i<os.length; i++) {
    		os[i] = new Object();
    	}
    }

    // call the run() method of any runnable to execute its code
    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {
        // 双数先取左边的
        if (philosopher % 2 == 0) {
        	int kuazi = 0;
			if (philosopher != os.length - 1) {
				kuazi = philosopher + 1;
			}
        	synchronized (os[philosopher]) {
        		synchronized (os[kuazi]) {
        			pickLeftFork.run();
					pickRightFork.run();
					eat.run();
					putLeftFork.run();
					putRightFork.run();
        		}
			}
        } 
        // 单数先取右边的
        else {
        	synchronized (os[philosopher + 1]) {
        		synchronized (os[philosopher]) {
        			pickLeftFork.run();
					pickRightFork.run();
					eat.run();
					putLeftFork.run();
					putRightFork.run();
        		}
			}
        }
    }
}
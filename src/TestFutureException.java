import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestFutureException {
	
	public static void main(String[] args) {
		Future f = Executors.newFixedThreadPool(5).submit(() -> {
			return 1;
		});
		
		try {
			System.out.println(f.get());
		} catch (InterruptedException | ExecutionException e) {
			if (e instanceof ExecutionException) {
				System.out.println(((ExecutionException)e).getCause());
			}
			e.printStackTrace();
		}
	}

}

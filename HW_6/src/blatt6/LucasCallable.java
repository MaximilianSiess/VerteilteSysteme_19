package blatt6;

import java.util.concurrent.Callable;

// Specific implementation of generic TaskCallable interface
public class LucasCallable implements Callable<Integer>, java.io.Serializable {

	private static final long serialVersionUID = 1L;
	int n;

	public LucasCallable(int n) {
		this.n = n;
	}

	@Override
	public Integer call() throws Exception {
		int result = lucas(n);
		System.out.println("Task called. Result: " + result);
		return result;
	}

	private int lucas(int value) {
		if (value == 0)
			return 2;
		if (value == 1)
			return 1;
		return lucas(value - 1) + lucas(value - 2);
	}
}

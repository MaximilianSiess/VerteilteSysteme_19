package blatt5;

import java.rmi.RemoteException;

public class ServiceProvider implements Service {

	@Override
	public int add(int value1, int value2) {
		return value1 + value2;
	}

	@Override
	public int sub(int value1, int value2) {
		return value1 - value2;
	}

	@Override
	public int mul(int value1, int value2) {
		return value1 * value2;
	}

	@Override
	public int lucas(int value) {
		if (value == 0)
			return 2;
		if (value == 1)
			return 1;
		return lucas(value - 1) + lucas(value - 2);
	}

	@Override
	public int calculate(Operation operation, int[] values) throws RemoteException {

		int result = 0;

		switch (operation) {
		case ADDITION:
			result = add(values[0], values[1]);
			break;
		case SUBSTRAKTION:
			result = sub(values[0], values[1]);
			break;
		case MULTIPLIKATION:
			result = mul(values[0], values[1]);
			break;
		case LUCAS:
			result = lucas(values[0]);
			break;
		}

		return result;
	}

	@Override
	public void deepThought(final String question, final DeepThougtInterface client) throws RemoteException {
		Thread helper = new Thread() {

			@Override
			public void run() {
				System.out.println("\nReceived deepThought request: " + question);
				System.out.println("Processing deepThought request ...");

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				String answer = "The answer to your question \"" + question + "\", is probably 42.";

				// Call the callback function
				try {
					client.notifyClient(answer);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		};
		helper.start();

		return;
	}
}
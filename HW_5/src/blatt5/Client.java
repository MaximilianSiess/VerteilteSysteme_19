package blatt5;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

public class Client {

	private static final String SERVER_NAME = "localhost";

	private static Random generator = new Random();

	public static void main(String[] args) {

		try {
			// 1) get registry connection
			Registry registry = LocateRegistry.getRegistry(SERVER_NAME, Server.REGISTRY_PORT_NUMBER);

			// 2) lookup service reference
			Service service = (Service) registry.lookup(Server.SERVICE_NAME);

			// 3) use service (reference can be used like a local instance)
			useService(service);

		} catch (NotBoundException nbe) {
			System.err.println("Service not available: " + nbe.getMessage());
		} catch (RemoteException re) {
			System.err.println("Error using remote service: " + re.getMessage());
		}
	}

	private static void useService(Service service) throws RemoteException {

		// Default, to avoid uninitialized error
		Operation operator = Operation.LUCAS;
		int[] operands;

		// Make 10 requests
		for (int i = 0; i < 10; i++) {
			System.out.println("Request " + (i + 1));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Choose operator
			int random = generator.nextInt(4);
			switch (random) {
			case 0:
				operator = Operation.ADDITION;
				break;
			case 1:
				operator = Operation.SUBSTRAKTION;
				break;
			case 2:
				operator = Operation.MULTIPLIKATION;
				break;
			case 3:
				operator = Operation.LUCAS;
				break;
			}

			if (operator.equals(Operation.LUCAS)) {
				random = generator.nextInt(20);
				operands = new int[] { random };
			} else {
				int random1 = generator.nextInt(1000);
				int random2 = generator.nextInt(1000);
				operands = new int[] { random1, random2 };

			}

			int result = service.calculate(operator, operands);

			System.out.print("Request: " + operator);

			if (operator.equals(Operation.LUCAS)) {
				System.out.printf("(%d) = %d\n", operands[0], result);
			} else {
				System.out.printf("(%d,%d) = %d\n", operands[0], operands[1], result);
			}
		}
	}
}

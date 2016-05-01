package blatt6_1;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

	private static final String SERVER_NAME = "localhost";

	public static void main(String[] args) {
		try {
			// 1) get registry connection
			Registry registry = LocateRegistry.getRegistry(SERVER_NAME, Server.REGISTRY_PORT_NUMBER);

			// 2) lookup service reference
			IService<Integer> service = (IService<Integer>) registry.lookup(Server.SERVICE_NAME);

			// 3) use service (reference can be used like a local instance)
			useService(service);

		} catch (NotBoundException nbe) {
			System.err.println("Service not available: ");
			nbe.printStackTrace();
		} catch (RemoteException re) {
			System.err.println("Error using remote service: ");
			re.printStackTrace();
		}
	}

	private static void useService(IService<Integer> service) {
		int rounds = 50;
		int lucas = 1;

		LucasCallable task;

		try {
			for (int i = 0; i < rounds; i++) {
				Thread.sleep(500);
				task = new LucasCallable(lucas + i);
				Job<Integer> job = service.submit(task);

				if (job == null) {
					System.out.println("Client: Task was rejected...");
				} else {
					System.out.println("Client: Task " + job.getId() + " was accepted.");
					while (!job.isDone()) {
						Thread.sleep(100);
					}
					System.out.println("Task " + job.getId() + " done, result: " + job.getResult());
				}

			}

		} catch (RemoteException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

package blatt6_2;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import blatt6_1.Job;
import blatt6_1.LucasCallable;

public class Client2 extends Thread {

	private final String SERVER_NAME;

	private final Dispatcher<Integer> dispatcher;

	public Client2(Dispatcher<Integer> dispatcher) {
		super();
		SERVER_NAME = "localhost";
		this.dispatcher = dispatcher;
	}

	@Override
	public void run() {
		try {
			// 1) get registry connection
			Registry registry = LocateRegistry.getRegistry(SERVER_NAME, dispatcher.getRegistryPortNumber());

			// 2) lookup service reference
			IDispatcher<Integer> service = (IDispatcher<Integer>) registry.lookup(dispatcher.getServiceName());

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

	private static void useService(IDispatcher<Integer> service) {
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

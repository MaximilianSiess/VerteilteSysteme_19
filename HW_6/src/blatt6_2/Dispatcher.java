package blatt6_2;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.naming.LimitExceededException;

import blatt6_1.IService;
import blatt6_1.Job;

public class Dispatcher<T> implements IDispatcher<T>, Serializable {

	private static final long serialVersionUID = 1L;
	private String SERVICE_NAME = "Dispatcher";
	private int REGISTRY_PORT_NUMBER;
	private List<IService> servicePool;
	private int currentService;

	public Dispatcher(int portNumber) {
		super();
		this.REGISTRY_PORT_NUMBER = portNumber;
		this.servicePool = new LinkedList<IService>();
		this.currentService = 0;
		try {
			System.out.println("Starting up dispatcher ... ");

			// 1) Export Service (make public available on some port)
			IDispatcher<Integer> stub = (IDispatcher<Integer>) UnicastRemoteObject.exportObject(this, 0);

			// 2) get RMI Registry reference (started by this application)
			Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT_NUMBER);

			// 3) bind service to corresponding name within the registry
			registry.rebind(SERVICE_NAME, stub);
			System.out.println("Dispatcher exported and bound!");

		} catch (RemoteException re) {
			System.err.println("Error publishing service: ");
			re.printStackTrace();
		} catch (Exception e) {
			System.out.println("Anything else failed:");
			e.printStackTrace();
		}

	}

	@Override
	public Job<T> submit(Callable<T> task) throws RemoteException, Exception {
		while (true) {
			synchronized (servicePool) {
				while (servicePool.isEmpty()) {
					// waits until at least one service has been registered
					servicePool.wait();
				}

				try {
					// forwarding task to services in round robin manner
					if (servicePool.size() < currentService) {
						return servicePool.get(currentService++).submit(task);
					} else {
						currentService = 0;
						return servicePool.get(currentService++).submit(task);
					}
				} catch (LimitExceededException e) {
					System.out.println("One of the servers is too busy...");
					// currentService has already been incremented by 1
				}
			}
		}
	}

	@Override
	public void register(Server2<T> server) {
		try {
			// 1) get registry connection
			Registry registry = LocateRegistry.getRegistry("localhost", server.getRegistryPortNumber());

			// 2) lookup service reference
			IService<Integer> service = (IService<Integer>) registry.lookup(server.getServiceName());

			// 3) register service
			synchronized (servicePool) {
				servicePool.add(service);
				servicePool.notify();
			}
		} catch (NotBoundException nbe) {
			System.err.println("Service not available: ");
			nbe.printStackTrace();
		} catch (RemoteException re) {
			System.err.println("Error using remote service: ");
			re.printStackTrace();
		}
	}

	@Override
	public int getRegistryPortNumber() {
		return REGISTRY_PORT_NUMBER;
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

}

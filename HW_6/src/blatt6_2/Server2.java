package blatt6_2;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import blatt6_1.IService;
import blatt6_1.ServiceProvider;

public class Server2<T> extends Thread implements IServer2 {

	private final String SERVICE_NAME;
	private final int REGISTRY_PORT_NUMBER;
	private IDispatcher<T> dispatcher;

	public Server2(int id, int port, IDispatcher<T> dispatcher) {
		super();
		this.SERVICE_NAME = "ComputationService" + id;
		this.REGISTRY_PORT_NUMBER = port;
		this.dispatcher = dispatcher;
	}

	@Override
	public void run() {
		try {

			System.out.println("Starting up service ... ");

			// 1) Create Service
			ServiceProvider<Integer> service = new ServiceProvider<Integer>();
			service.start();

			// 2) Export Service (make public available on some port)
			IService<Integer> stub = (IService<Integer>) UnicastRemoteObject.exportObject(service, 0);

			// 3) get RMI Registry reference (started by this application)
			Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT_NUMBER);

			// 4) bind service to corresponding name within the registry
			registry.rebind(SERVICE_NAME, stub);

			System.out.println("Service exported and bound!");
			dispatcher.register(this);
			System.out.println("Server " + SERVICE_NAME + " registered!");

		} catch (RemoteException re) {
			System.err.println("Error publishing service: ");
			re.printStackTrace();
		} catch (Exception e) {
			System.out.println("Anything else failed:");
			e.printStackTrace();
		}
	}

	@Override
	public int getRegistryPortNumber() throws RemoteException {
		return REGISTRY_PORT_NUMBER;
	}

	@Override
	public String getServiceName() throws RemoteException {
		return SERVICE_NAME;
	}
}

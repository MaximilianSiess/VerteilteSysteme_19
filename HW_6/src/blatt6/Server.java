package blatt6;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

	public static final String SERVICE_NAME = "ComputationService";

	public static final int REGISTRY_PORT_NUMBER = 1024;

	public static void main(String[] args) {

		try {

			System.out.println("Starting up service ... ");

			// 1) Create Service
			Service service = new ServiceProvider();
                        service.setLimit(3);

			// 2) Export Service (make public available on some port)
			Service stub = (Service) UnicastRemoteObject.exportObject(service, 0);

			// 3) get rmi Registry reference (started by this application)
			Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT_NUMBER);

			// 4) bind service to corresponding name within the registry
			registry.rebind(SERVICE_NAME, stub);

			System.out.println("Service exported and bound!");

		} catch (RemoteException re) {
			System.err.println("Error publishing service: ");
                        re.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Anything else failed:");
                    e.printStackTrace();
                }
	}
}

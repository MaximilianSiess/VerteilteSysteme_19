package blatt6;

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
			Service service = (Service) registry.lookup(Server.SERVICE_NAME);

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
    
    private static void useService(Service service) {
        int rounds = 600;
        int lucas = 15;
        
        LucasCallable task;
        
        try {
            for (int i = 0; i < rounds; i++) {
                task = new LucasCallable(lucas + (i/10));
                Job taskjob = service.submit(task);
                
                if (taskjob == null) {
                    System.out.println("Client: Task was rejected...");
                } else {
                    System.out.println("Client: Task was accepted.");
                    while (!taskjob.isDone()) {
                        Thread.sleep(10);
                    }
                    System.out.println("Job done, result: " + taskjob.getResult());
                } 
                
            }
                    
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

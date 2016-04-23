package blatt5;

import java.io.Serializable;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class DeepThougtClient implements Runnable, DeepThougtInterface, Serializable {

	protected DeepThougtClient() throws RemoteException {
	}

	private static final long serialVersionUID = -1077271795371264921L;

	private final String SERVER_NAME = "localhost";

	private String answer = null;

	@Override
	public void run() {

		try {
			// 0) exporting itself
			UnicastRemoteObject.exportObject(this, 0);
			System.out.println("Client has exported itsself!");

			// 1) get registry connection
			Registry registry = LocateRegistry.getRegistry(SERVER_NAME, Server.REGISTRY_PORT_NUMBER);

			// 2) lookup service reference
			Service service = (Service) registry.lookup(Server.SERVICE_NAME);

			// 3) use the deepThought service
			service.deepThought("Is this solution correct?", this);
			System.out.println("Client is counting:");

			int i = 0;
			while (true) {
				if (answer != null) {
					System.out.println(answer);
					break;
				}
				if (i % 100 == 0) {
					System.out.println(i / 10 + " ");
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i++;
			}

		} catch (NotBoundException nbe) {
			System.err.println("Service not available: " + nbe.getMessage());
		} catch (RemoteException re) {
			System.err.println("Error using remote service: " + re.getMessage());
		} finally {
			try {
				UnicastRemoteObject.unexportObject(this, true);
				System.out.println("The Client has finished successfully!");
			} catch (NoSuchObjectException e) {
				System.out.println("Error unexporting the Client object: " + e.getMessage());
			}
		}
	}

	@Override
	public void notifyClient(String answer) {
		this.answer = answer;
	}

	public static void main(String[] args) {

		try {
			DeepThougtClient c1 = new DeepThougtClient();
			new Thread(c1).start();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
}

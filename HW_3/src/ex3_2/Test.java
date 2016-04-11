package ex3_2;

public class Test {

	public static void main(String[] args) {

		final ServiceAnnouncer server = new ServiceAnnouncer(1234);
		// Hook does not work inside Eclipse

		// Add a hook for shutdown exception handling
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Shutdown hook activated!");
				server.stopServiceAnnouncer();
			}
		});
		Client client1 = new Client(new ServiceLocator(1234, 250), 0);
		Client client2 = new Client(new ServiceLocator(1234, 250), 1);
		server.start();
		ServerShutdown shutdown = new ServerShutdown(server);
		shutdown.start();

		// Start some clients
		client1.start();
		client2.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client1.stopClient();
		client2.stopClient();
	}
}

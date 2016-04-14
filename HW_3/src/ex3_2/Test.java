package ex3_2;

public class Test {

	public static void main(String[] args) {

		//final ServiceAnnouncer server = new ServiceAnnouncer(1234);
		ServiceAnnouncer serverAnnouncer = new ServiceAnnouncer(1234, 4321);
		final Server server = new Server(serverAnnouncer, 0);
		
		// Shutdown hook to catch ctrl+C TERM signal
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run(){
				System.out.println("Client: TERM Signal recieved.");
				server.stopServer();
			}
		});
		
		Client client1 = new Client(new ServiceLocator(1234, 250), 0);
		Client client2 = new Client(new ServiceLocator(1234, 250), 1);
		server.start();

		// Start some clients
		client1.start();
		client2.start();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client1.stopClient();
		client2.stopClient();
	}
}

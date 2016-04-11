package ex3_2;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client extends Thread {

	private final ServiceLocator locator;
	private boolean running = true;
	private final int clientID;
	InetAddress adress;

	public Client(ServiceLocator locator, int id) {
		this.locator = locator;
		clientID = id;
	}

	@Override
	public void run() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Shutdown hook activated!");
				running = false;
			}
		});
		System.out.println("CLient" + clientID + " is started.");
		try {
			adress = locator.locate();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Adress: " + adress);
		while (running) {
			try {
				sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Client" + clientID + " :Still alive!");
		}
	}

	public void stopClient() {
		running = false;
	}
}

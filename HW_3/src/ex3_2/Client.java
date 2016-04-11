package ex3_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread {

	private final ServiceLocator locator;
	private static Socket socket;
	private boolean running = true;
	private final int clientID;
	InetAddress address;

	public Client(ServiceLocator locator, int id) {
		this.locator = locator;
		clientID = id;
	}

	@Override
	public void run() {
		// Shutdown hook to catch ctrl+C TERM signal
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run(){
						System.out.println("Client: TERM Signal recieved.");
						stopClient();
					}
				});
		
		System.out.println("Client" + clientID + " is started.");
		try {
			locator.locate();
			address = locator.getAddress();
			int portnumber = locator.getPort();
			socket = new Socket(address, portnumber);
		
			System.out.println("Adress: " + address);
			
			BufferedReader ClientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter ClientOut = new PrintWriter(socket.getOutputStream(), true);
			
			while (running) {
				try {
					sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Client" + clientID + " : sending via TCP on port " + portnumber);
				ClientOut.println("TCP Ping");
				System.out.println("Client" + clientID + " : sent via TCP");
				String response = ClientIn.readLine();
				System.out.println("Client" + clientID + " >" + response);
			}
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Client" + clientID + " stopped.");
	}

	public void stopClient() {
		running = false;
	}
}

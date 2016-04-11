package ex3_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

	private final ServiceAnnouncer announcer;
	private final ServerShutdown shutdown;
	private static ServerSocket socket;
	private static Socket connection = null;
	private boolean running = true;
	private final int serverID;
	InetAddress adress;

	public Server(ServiceAnnouncer announcer, int id) {
		serverID = id;
		this.announcer = announcer;
		shutdown = new ServerShutdown(this);
	}

	@Override
	public void run() {
		announcer.start();
		shutdown.start();
		System.out.println("Server" + serverID + " is started.");
		
		try {
			socket = new ServerSocket();
			socket.setReuseAddress(true);
			socket.bind(new InetSocketAddress(announcer.getPort()), 10);
			
			while (running) {
				System.out.println("Server" + serverID + " waiting for connection");
				connection = socket.accept();
				System.out.println("Server" + serverID + " : Connection received from " + connection.getInetAddress().getHostName());
				
				BufferedReader ServerIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				PrintWriter ServerOut = new PrintWriter(connection.getOutputStream(), true);
				
				String inString = ServerIn.readLine();
				
				if (inString.compareTo("TCP Ping") == 0) {
					ServerOut.println("TCP Response Ping");
				}
				
				sleep(5000);
				System.out.println("Server" + serverID + " : Answered ping from " + announcer.getPort());
			}
			
			connection.close();
			socket.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Server" + serverID + " stopped.");
	}

	public void stopServer() {
		running = false;
	}
}

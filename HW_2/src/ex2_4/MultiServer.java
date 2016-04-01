package ex2_4;

import java.io.*;
import java.net.*;
import ex2_3.Protocol;
import sun.tracing.ProviderSkeleton;

public class MultiServer implements Runnable {
	
	private static ServerSocket providerSocket;
	private static Socket connection = null;
	private static boolean running = true;
	
	public void run() {
		try {
			Protocol.InitServer(connection);
			
			boolean socketOpen = true;
			
			while(socketOpen) {
				socketOpen = Protocol.reply();
			}
		} catch (IOException e) {
			System.out.println("Could not handle reply!");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		//TODO(Max): Hook does not work inside Eclipse
		
		// Add a hook for shutdown exception handling
		Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("Shutdown hook activated!");
                running = false;           
            }
        });
		
		// Server logic
		try {
			providerSocket = new ServerSocket();
            providerSocket.setReuseAddress(true);
            providerSocket.bind(new InetSocketAddress(Protocol.getPortNumber()), 10);
			
			while (running) {
				System.out.println("Waiting for connections...");
				connection = providerSocket.accept();
				System.out.println("Connection received from " + connection.getInetAddress().getHostName());
				new Thread(new MultiServer()).start();
			}
			connection.close();
		} catch (IOException e) {
			System.out.println("Could not establish a connection!");
			e.printStackTrace();
			
		}
	}

}

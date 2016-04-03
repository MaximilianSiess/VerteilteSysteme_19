package ex2_4;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ex2_3.Protocol;

public class MultiServer{

    //TODO: ExecutorService only works for one thread at a time...

	private static ServerSocket providerSocket;

	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();

		// Add a hook for shutdown exception handling
		// NOTE: Hook does not work inside Eclipse
        // must send a TERM signal with kill
		/*
		Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("Shutdown hook activated!");
                System.out.println("Shutting Server down..");
                try {
                	connection.close();
                } catch (IOException e) {
                	System.out.println("Could not close server socket.");
                	e.printStackTrace();
                } catch (NullPointerException e) {
                	System.out.println("No connections found.");
            	} finally {
	                executor.shutdown();
	                // Wait for all threads to finish
	    			while (!executor.isTerminated()) {
	    				System.out.print(".");
	    			}
                }
            }
        });
        */

		// Server logic
		try {
			providerSocket = new ServerSocket();
            providerSocket.setReuseAddress(true);
            providerSocket.bind(new InetSocketAddress(Protocol.getPortNumber()), 10);

			while (true) {
				System.out.println("Waiting for connections...");

				HandleRequest newRequest = new HandleRequest();
				newRequest.RequestHandlerThread(providerSocket.accept());

				executor.execute(newRequest);
			}
		} catch (IOException e) {
			System.out.println("Could not establish a connection!");
			e.printStackTrace();
		}
	}

}

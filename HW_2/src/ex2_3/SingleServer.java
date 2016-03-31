package ex2_3;

import org.json.*;
import java.io.*;
import java.net.*;

public class SingleServer {
	
	private ServerSocket providerSocket;
	private static Socket connection = null;
	private String request = "";
	private static boolean running = true;
	
	void establishConnection() {
		try{
            providerSocket = new ServerSocket(Protocol.getPortNumber(), 10);

            System.out.println("Waiting for connection");
            connection = providerSocket.accept();
            
            System.out.println("Connection received from " + connection.getInetAddress().getHostName());

        }
        catch(IOException ioException){
            ioException.printStackTrace();
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
                try {
                	Protocol.closeSocket(connection);
                }
                catch (IOException e) {
                	System.out.println("Could not close ServerSocket!");
                	e.printStackTrace();
                }
                
            }
        });
		
		// Server logic
		SingleServer server = new SingleServer();
		
		try {
			server.establishConnection();
			Protocol.InitServer(connection);
			
			while(running) {
				Protocol.reply();
			}
			
			Protocol.closeSocket(connection);
		} catch (IOException e) {
			System.out.println("Could not establish a connection!");
			e.printStackTrace();
			
		}
	}

}

package ex2_3;

import org.json.*;
import java.io.*;
import java.net.*;

public class SingleServer {
	
	private ServerSocket providerSocket;
	private Socket connection = null;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String request = "";
	
	void run() {
		/*
		try{
            providerSocket = new ServerSocket(2004, 10);

            System.out.println("Waiting for connection");
            connection = providerSocket.accept();
            
            System.out.println("Connection received from " + connection.getInetAddress().getHostName());
            

            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
            
            //sendMessage("Connection successful");
            
            //Wait for the client to send a request
            while(request.equals("")){
                try{
                    request = (String)in.readObject();
                    System.out.println("client>" + request);
                }
                catch(ClassNotFoundException classnot){
                    System.err.println("Data received in unknown format");
                }
            }
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            //4: Closing connection
            try{
                in.close();
                out.close();
                providerSocket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
        */
	}

	public static void main(String[] args) {
		SingleServer server = new SingleServer();
		
		while(true) {
			server.run();
		}

	}

}

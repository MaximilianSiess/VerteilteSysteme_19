package ex10_1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XORApp {
    private static ServerSocket socket;
    private static Socket connection = null;
    private static boolean running = true;
    InetAddress adress;
    
    public static void main(String[] args) {
        boolean serverExists = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            System.out.println("Server (s) or Client (c)?");
            String input = br.readLine();
            if (input.compareTo("s") == 0) {
                serverExists = false;
            } else {
                serverExists = true;
            }
        } catch (IOException ex) {
            Logger.getLogger(XORApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // ====================================================================== Server running - become CLIENT
        if (serverExists) {
            try {
                connection = new Socket("localhost", 1337);

                InputStream ClientIn = connection.getInputStream();
                OutputStream ClientOut = connection.getOutputStream();
                
                while (running) {
                        Thread.sleep(500);            
                        
                        System.out.println("Enter message to send, or \"exit\" to quit:");  
                        String message = br.readLine();
                        
                        // Check if client is supposed to close
                        if (message.compareTo("exit") == 0) {
                            running = false;
                        } else {
                            byte[] data = message.getBytes(StandardCharsets.UTF_8);
                            
                            ClientOut.write(data);
                            ClientOut.flush();
                            System.out.println("Sent server the encrypted message.");
                            Thread.sleep(500);
                            byte[] response = new byte[64];
                            ClientIn.read(response);
                            
                            String decrypted_response = new String(response, StandardCharsets.UTF_8);
                            System.out.println("Server response: " + decrypted_response);
                        }
                }
                connection.close();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(XORApp.class.getName()).log(Level.SEVERE, null, ex);
            } 
            System.out.println("Client stopped.");
        } else { // ============================================================= No server running - become SERVER
            try {
                socket = new ServerSocket();
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(1337), 10);

                System.out.println("Server waiting for connection");
                connection = socket.accept();
                System.out.println("Server: Connection received from " + connection.getInetAddress().getHostName());
                InputStream ServerIn = connection.getInputStream();
                OutputStream ServerOut = connection.getOutputStream();
                
                while (running) {
                        byte[] message = new byte[64];
                        ServerIn.read(message);
                        String encrypted_message = new String(message, StandardCharsets.UTF_8);
                        System.out.println("Recieved message from client: " + encrypted_message);
                        String decrypted_message = new String(message, StandardCharsets.UTF_8);
                        System.out.println("Decrypted message: " + decrypted_message);
                        
                        String response_string = "Successfully recieved " + decrypted_message + "!";
                        byte[] response = response_string.getBytes(StandardCharsets.UTF_8);

                        ServerOut.write(response);
                        ServerOut.flush();
                }

                connection.close();
                socket.close();
            } catch (IOException | NullPointerException e) {
                System.out.println("Client might have closed, or an error has occured.");
            }
            System.out.println("Server stopped.");
        }      
    }
}

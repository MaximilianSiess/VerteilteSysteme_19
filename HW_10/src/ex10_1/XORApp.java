package ex10_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XORApp {
    private static ServerSocket socket;
    private static Socket connection = null;
    private static boolean running = true;
    InetAddress adress;

    public static String encode(String s, String key) {
        return new String(xorWithKey(s.getBytes(), key.getBytes())).replaceAll("\r", "").replaceAll("\n", "");
    }

    public static String decode(String s, String key) {
        return new String(xorWithKey(s.getBytes(), key.getBytes())).replaceAll("\r", "").replaceAll("\n", "");
    }

    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i%key.length]);
        }
        return out;
    }
    
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
            // The key for the XOR encryption
            String key = "Iamakeydasfasdfs!";
            try {
                connection = new Socket("localhost", 1337);

                BufferedReader ClientIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                PrintWriter ClientOut = new PrintWriter(connection.getOutputStream(), true);
                
                while (running) {
                        Thread.sleep(500);            
                        
                        System.out.println("Enter message to send, or \"exit\" to quit:");  
                        String message = br.readLine();
                        
                        // Check if client is supposed to close
                        if (message.compareTo("exit") == 0) {
                            running = false;
                        } else {
                            message = encode(message, key);                            
                            ClientOut.println(message);
                            ClientOut.flush();
                            System.out.println("Sent server the encrypted message.");
                            Thread.sleep(500);
                            String response = ClientIn.readLine();
                            String decrypted_response = decode(response, key);
                            System.out.println("Server response: " + decrypted_response);
                        }
                }
                connection.close();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(XORApp.class.getName()).log(Level.SEVERE, null, ex);
            } 
            System.out.println("Client stopped.");
        } else { // ============================================================= No server running - become SERVER
            // The key for the XOR encryption
            String key = "Iamakeydasfasdfs!";
            try {
                socket = new ServerSocket();
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(1337), 10);

                System.out.println("Server waiting for connection");
                connection = socket.accept();
                System.out.println("Server: Connection received from " + connection.getInetAddress().getHostName());
                BufferedReader ServerIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		PrintWriter ServerOut = new PrintWriter(connection.getOutputStream(), true);
                
                while (running) {
                        String encrypted_message = ServerIn.readLine();
                        System.out.println("Recieved message from client: " + encrypted_message);
                        String decrypted_message = decode(encrypted_message, key);
                        System.out.println("Decrypted message: " + decrypted_message);
                        
                        String response_string = decrypted_message;
                        String response = encode(response_string, key);
                        ServerOut.println(response);
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

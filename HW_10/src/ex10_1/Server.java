package ex10_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ServerSocket socket;
    private static Socket connection = null;
    private static boolean running = true;
    InetAddress adress;
    
    public static void main(String[] args) {
        try {
            socket = new ServerSocket();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(1337), 10);

            while (running) {
                    System.out.println("Server waiting for connection");
                    connection = socket.accept();
                    System.out.println("Server: Connection received from " + connection.getInetAddress().getHostName());

                    BufferedReader ServerIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    PrintWriter ServerOut = new PrintWriter(connection.getOutputStream(), true);

                    String inString = ServerIn.readLine();

                    if (inString.compareTo("TCP Ping") == 0) {
                            ServerOut.println("TCP Response Ping");
                    }

                    Thread.sleep(1000);
                    System.out.println("Server: Answered ping.");
            }

            connection.close();
            socket.close();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server stopped.");
    }
}

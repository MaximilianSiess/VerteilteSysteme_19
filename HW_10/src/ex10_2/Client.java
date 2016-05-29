package ex10_2;

import ex10_1.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
    private static Socket socket;
    private static boolean running = true;

    public static void main(String[] args) {
        System.out.println("Client is started.");
        try {
                socket = new Socket("localhost", 1337);

                BufferedReader ClientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter ClientOut = new PrintWriter(socket.getOutputStream(), true);

                while (running) {
                        try {
                                Thread.sleep(500);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }
                        System.out.println("Client: sending via TCP on port 1337");
                        ClientOut.println("TCP Ping");
                        System.out.println("Client: sent via TCP");
                        String response = ClientIn.readLine();
                        System.out.println("Client >" + response);
                }
                socket.close();
        } catch (UnknownHostException e) {
        } catch (IOException e) {
        }
        System.out.println("Client stopped.");
    }
}

package ex3_2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class ServiceLocator implements Runnable {

	DatagramSocket socket;
	DatagramPacket packet;
	int portnumber;
	
	public ServiceLocator(int port) {
		/*
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println("Could not open socket:");
			e.printStackTrace();
		}*/
		
		portnumber = port;
	}
	
	public void run() {
		try {
			//socket.bind(new InetSocketAddress(portnumber));
			//socket.connect(InetAddress.getByName("0.0.0.0"), portnumber);
			socket = new DatagramSocket(portnumber, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
			
			boolean running = true;
			
			while (running) {
				System.out.println(getClass().getName() + ">>> Waiting for broadcast packets...");
				packet= new DatagramPacket (new byte[15000], 0);
				socket.receive(packet);
				
				//Packet received
				System.out.println(getClass().getName() + ">>> Discovery packet received from: " + packet.getAddress().getHostAddress());
				System.out.println(getClass().getName() + ">>> Packet received; data: " + new String(packet.getData()));

				//See if the packet holds the right command (message)
		        String message = new String(packet.getData()).trim();
		        if (message.equals("DISCOVER_FUIFSERVER_REQUEST")) {
		        	byte[] sendData = "DISCOVER_FUIFSERVER_RESPONSE".getBytes();

			        //Send a response
			        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
			        socket.send(sendPacket);
		
			        System.out.println(getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
		        }
			}
			
			
            socket.send (packet);
            packet.setLength(100);
            socket.receive (packet);
            socket.close ();
            byte[] data = packet.getData ();
            String time=new String(data);
            System.out.println(time);
		} catch (SocketException e) {
			System.out.println("Could not open socket:");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not read from connection:");
			e.printStackTrace();
		/*} catch (CloseException e) {	// Custom exception
			System.out.println("Shutting down..."); */
		} finally {
			socket.close();
		}
	}
	
}

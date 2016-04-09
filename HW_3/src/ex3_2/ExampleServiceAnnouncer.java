package ex3_2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class ExampleServiceAnnouncer implements Runnable{
	DatagramSocket socket;
	DatagramPacket packet;
	int portnumber;
	
	public ExampleServiceAnnouncer(int port) {
		portnumber = port;
	}
	
	public void run() {
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			
			byte[] sendData = "DISCOVER_FUIFSERVER_REQUEST".getBytes();

			  //Try the 255.255.255.255 first
			  try {
			    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
			    socket.send(sendPacket);
			    System.out.println(getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
			  } catch (Exception e) {
			  }

			  // Broadcast the message over all the network interfaces
			  Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
			  while (interfaces.hasMoreElements()) {
			    NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

			    if (networkInterface.isLoopback() || !networkInterface.isUp()) {
			      continue; // Don't want to broadcast to the loopback interface
			    }

			    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
			      InetAddress broadcast = interfaceAddress.getBroadcast();
			      if (broadcast == null) {
			        continue;
			      }

			      // Send the broadcast package!
			      try {
			        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
			        socket.send(sendPacket);
			      } catch (Exception e) {
			      }

			      System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
			    }
			  }

			  System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");

			  //Wait for a response
			  byte[] recvBuf = new byte[15000];
			  DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
			  socket.receive(receivePacket);

			  //We have a response
			  System.out.println(getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

			  //Check if the message is correct
			  String message = new String(receivePacket.getData()).trim();
			
		} catch (SocketException e) {
			System.out.println("Could not open announcer socket:");
			e.printStackTrace();
		} catch (UnknownHostException e) {
			System.out.println("Could not connect:");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Communication error:");
			e.printStackTrace();
		} finally {
			socket.close();
		}
		
		packet = new DatagramPacket (new byte[1], 1);
		boolean running = true;
	}
}

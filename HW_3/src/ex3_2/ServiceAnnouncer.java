package ex3_2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServiceAnnouncer extends Thread {

	private DatagramSocket socket;
	private final int port;
	private final byte[] buffer = new byte[12];
	private boolean running = true;

	public ServiceAnnouncer(final int port) {
		this.port = port;
	}

	@Override
	public void run() {
		System.out.println("Server is started!");
		try {

			// Open the UDP port
			socket = new DatagramSocket(port);

			while (running) {
				// Waiting for packages of clients
				final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

				socket.receive(packet);

				// Read received data
				final String data = new String(packet.getData()).trim();

				// Test if the message is "Ping"
				if (data.equalsIgnoreCase("Ping")) {
					System.out.println("Ping sent from client : " + packet.getAddress().getHostAddress() + " with Port:"
							+ packet.getPort());

					// Send a response message to the ServiceLocator
					final DatagramPacket response = new DatagramPacket(InetAddress.getLocalHost().getAddress(),
							InetAddress.getLocalHost().getAddress().length, packet.getAddress(), packet.getPort());
					socket.send(response);
				} else {
					System.out.println("Invalid message: " + data);
				}

			}
		} catch (SocketException e) {
			System.out.println("Port is already in use!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Communication error!");
			e.printStackTrace();
		} finally {
			socket.close();
		}
	}

	public void stopServiceAnnouncement() {
		running = false;
	}

}

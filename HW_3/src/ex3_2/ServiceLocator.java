package ex3_2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServiceLocator {
	private final int port;
	private final int timeout;

	public ServiceLocator(int port, int timeout) {
		this.port = port;
		this.timeout = timeout;
	}

	public InetAddress locate() throws UnknownHostException {
		try {
			// Create ping
			byte[] content = "Ping".getBytes();
			DatagramSocket socket = new DatagramSocket();
			// Broadcast ping
			byte adr = (byte) 255;
			byte[] address = { adr, adr, adr, adr };
			InetAddress internetAdress = InetAddress.getByAddress(address);
			DatagramPacket packet = new DatagramPacket(content, content.length, internetAdress, port);
			// Send ping
			socket.send(packet);
			// Wait for response until timeout
			socket.setSoTimeout(timeout);
			socket.receive(packet);

			return packet.getAddress();

		} catch (final IOException e) {
			throw new UnknownHostException();
		}
	}

}

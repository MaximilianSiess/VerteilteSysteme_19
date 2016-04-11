package ex3_2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServiceLocator {
	private final int UDPport;
	private final int timeout;

	InetAddress serveraddress;
	int serverTCPport;
	
	public ServiceLocator(int port, int timeout) {
		this.UDPport = port;
		this.timeout = timeout;
	}

	public void locate() throws UnknownHostException {
		try {
			// Create ping
			byte[] content = "Ping".getBytes();
			DatagramSocket socket = new DatagramSocket();
			// Broadcast ping
			byte adr = (byte) 255;
			byte[] address = { adr, adr, adr, adr };
			InetAddress internetAdress = InetAddress.getByAddress(address);
			DatagramPacket packet = new DatagramPacket(content, content.length, internetAdress, UDPport);
			// Send ping
			socket.send(packet);
			// Wait for response until timeout
			socket.setSoTimeout(timeout);
			socket.receive(packet);
			
			socket.close();
			
			serveraddress = packet.getAddress();
			serverTCPport = packet.getPort();

		} catch (final IOException e) {
			throw new UnknownHostException();
		}
	}
	
	public int getPort() {
		return serverTCPport;
	}
	
	public InetAddress getAddress() {
		return serveraddress;
	}

}

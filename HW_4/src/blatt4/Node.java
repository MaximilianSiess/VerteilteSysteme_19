package blatt4;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Node {
	private int port;
	private int n;
	private InetAddress adress;
	private Node[] table;
	private Socket socket;
	private ServerSocket serverSocket;

	public Node(int port, InetAddress adress, int n) {
		this.port = port;
		this.n = n;
		this.adress = adress;
		this.table = new Node[n];
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Node(int port, InetAddress adress, int n, Node node) {
		this.port = port;
		this.n = n;
		this.adress = adress;
		this.table = new Node[n];
		table[0] = node;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getPort() {
		return port;
	}

	public InetAddress getAdress() {
		return adress;
	}

	public Socket getSocket() {
		return socket;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public int getN() {
		return n;
	}

	public Node[] getTable() {
		return table;
	}

	public void setTable(Node[] table) {
		this.table = table;
	}

}

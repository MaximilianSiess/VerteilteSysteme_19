package blatt4;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Node extends Thread {

	private int n;
	private NodeAddress ownAddress;
	private List<NodeAddress> table;
	private ServerSocket serverSocket;
	private boolean isActive = true;

	public Node(int port, InetAddress address, int n) {
		this.n = n;
		this.ownAddress = new NodeAddress(address, port);
		this.table = new LinkedList<NodeAddress>();
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setReuseAddress(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Node(int port, InetAddress address, int n, Node node) {
		this.n = n;
		this.ownAddress = new NodeAddress(address, port);
		this.table = new LinkedList<NodeAddress>();
		table.add(node.getNodeAddress());
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setReuseAddress(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void mergeTables(LinkedList<NodeAddress> exchangeTable) {
		System.out.println("Node " + ownAddress.getPort() + " got:");
		printTable(exchangeTable);
		synchronized (table) {
			for (int i = 0; i < exchangeTable.size(); i++) {
				NodeAddress newNode = exchangeTable.get(i);
				if (!(newNode.equals(ownAddress) || table.contains(newNode))) {
					table.add(exchangeTable.get(i));
				}
			}
			while (table.size() > n) {
				table.remove(Math.random() * (table.size() - 1));
			}
			System.out.println("Node " + ownAddress.getPort() + " merged tables.");
			printTable(table);
		}
	}

	@Override
	public void run() {
		Runnable requestHandler = new Runnable() {
			@Override
			public void run() {
				while (isActive) {
					Socket socket = null;
					ObjectInputStream in = null;
					ObjectOutputStream out = null;
					try {
						System.out.println("before accept");
						socket = serverSocket.accept();
						System.out.println("after accept");
						in = new ObjectInputStream(socket.getInputStream());
						out = new ObjectOutputStream(socket.getOutputStream());

						// receive message from connection
						LinkedList<NodeAddress> exchangeTable = (LinkedList<NodeAddress>) in.readObject();
						synchronized (table) {
							out.writeObject(table);
							mergeTables(exchangeTable);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} finally {
						if (socket != null) {
							try {
								socket.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						try {
							if (in != null) {
								in.close();
							}
							if (out != null) {
								out.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		new Thread(requestHandler).start();

		while (!isInterrupted()) {
			if (!table.isEmpty()) {
				NodeAddress other = table.get(0);
				exchangeTables(other);
			}
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// stop Node
				interrupt();
			}
		}
		isActive = false;
	}

	private void exchangeTables(NodeAddress other) {
		Socket socket = null;
		try {
			socket = new Socket(other.getAddress(), other.getPort());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			System.out.println("Initialized streams");
			synchronized (table) {
				table.add(ownAddress);
				out.writeObject(table);
				System.out.println("Node " + ownAddress.getPort() + " sent table to Node " + other.getPort());
				LinkedList<NodeAddress> exchangeTable = (LinkedList<NodeAddress>) in.readObject();
				System.out.println("Node " + ownAddress.getPort() + " received table from Node " + other.getPort());
				table.remove(ownAddress);
				mergeTables(exchangeTable);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	private void printTable(List<NodeAddress> table) {
		for (NodeAddress nodeAddress : table) {
			System.out.println("Node " + ownAddress.getPort() + ": " + nodeAddress);
		}
	}

	public NodeAddress getNodeAddress() {
		return ownAddress;
	}

}

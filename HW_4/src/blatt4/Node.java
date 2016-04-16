package blatt4;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

public class Node extends Thread {

	private int n;
	private NodeAddress ownAddress;
	private List<NodeAddress> table;
	private ServerSocket serverSocket;
	private boolean isActive = true;

	public Node(String name, int port, InetAddress address, int n) {
		this.n = n;
		this.ownAddress = new NodeAddress(address, port, name);
		this.table = new LinkedList<NodeAddress>();
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setReuseAddress(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Node(String name, int port, InetAddress address, int n, Node node) {
		this.n = n;
		this.ownAddress = new NodeAddress(address, port, name);
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
						
						// Check if it is a search request
						if (exchangeTable != null && exchangeTable.get(0).getSearchQuest()) {
							NodeAddress searchResult = search(exchangeTable.get(0).getName());
							if (searchResult == null) {
								System.out.println("Node " + ownAddress.getName() + " got a search request, but " + exchangeTable.get(0).getName() + " was not found!");
							} else {
								System.out.println("Node " + ownAddress.getName() + " got a search request, and " + exchangeTable.get(0).getName() + " was found!");
							}
							out.writeObject(searchResult);
						} else {	// It is a table exchange request
							synchronized (table) {
								out.writeObject(table);
								mergeTables(exchangeTable);
							}
						}
					} catch (SocketException e) {
						System.out.println(ownAddress.getName() + ": Socket closed!");
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
				try {
					serverSocket.close();	// Stops requestHandler
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
	
	public NodeAddress search(String destination) {
		// If we are the destination: SUCCESS!
		if (ownAddress.getName().compareTo(destination) == 0) {
			System.out.println("Found Destination " + destination + "!");
			return ownAddress;
		}
		
		// See if destination is in table
		for (NodeAddress nodeAddress : table) {
			if (nodeAddress.getName().compareTo(destination) == 0) {
				//Send to this node, success imminent... unless there is a timeout, then FAIL
				return sendSearchRequest(nodeAddress, destination);
			}
		}
		// Destination is not in table... send search request to every known node
		for (NodeAddress nodeAddress : table) {
			NodeAddress result = sendSearchRequest(nodeAddress, destination);
			if (result != null) {
				return result;
			}
		}
		
		// None of the nodes in table found destination... return failure
		return null;
	}
	
	private NodeAddress sendSearchRequest(NodeAddress other, String destination) {
		Socket socket = null;
		LinkedList<NodeAddress> result = null;
		try {
			socket = new Socket(other.getAddress(), other.getPort());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());	
			System.out.println("Initialized streams");
			// Create list with search request
			LinkedList<NodeAddress> requestWrapper = new LinkedList<NodeAddress>();
			NodeAddress request = new NodeAddress(null, 0, destination);
			request.makeSearchNodeAddress();
			requestWrapper.add(request);
			out.writeObject(requestWrapper);
			out.flush();
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			result = (LinkedList<NodeAddress>) in.readObject();
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
		
		if (result == null) {
			return null;
		}
		
		return result.get(0);
	}

}

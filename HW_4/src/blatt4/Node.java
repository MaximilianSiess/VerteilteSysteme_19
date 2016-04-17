package blatt4;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Node extends Thread {

	private final int n;
	private final String name;
	private final NodeAddress ownAddress;
	private List<NodeAddress> table;
	private ServerSocket serverSocket;
	private boolean isActive = true;
	private Map<NodeAddress, List<String>> requests = new HashMap<NodeAddress, List<String>>();

	public Node(String name, int port, InetAddress address, int n) {
		this.n = n;
		this.name = name;
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
		this.name = name;
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
		printTable(exchangeTable, false);
		synchronized (table) {
			for (int i = 0; i < exchangeTable.size(); i++) {
				NodeAddress newNode = exchangeTable.get(i);
				if (!(newNode.equals(ownAddress) || table.contains(newNode))) {
					table.add(exchangeTable.get(i));
				}
			}
			while (table.size() > n) {
				table.remove((int) (Math.random() * (table.size() - 1)));
			}
			System.out.println(name + " merged tables.");
			printTable(table, true);
		}
	}

	@Override
	public void run() {
		ExecutorService executor = Executors.newFixedThreadPool(n * 3);
		executor.submit(new Runnable() {

			@Override
			public void run() {
				while (isActive) {
					Socket socket = null;

					try {
						System.out.println(name + " before accept");
						socket = serverSocket.accept();
						System.out.println(name + " after accept");

						executor.submit(new RequestHandler(socket));
					} catch (SocketException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		while (!isInterrupted()) {
			synchronized (table) {
				if (!table.isEmpty()) {
					NodeAddress other = table.get((int) (Math.random() * (table.size() - 1)));
					exchangeTables(other);
				}
			}
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// stop Node
				try {
					// stops requestHandler
					serverSocket.close();
				} catch (IOException e1) {
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
			synchronized (table) {
				table.add(ownAddress);
				out.writeObject(table);
				System.out.println(name + " sent table to " + other.getName());
				LinkedList<NodeAddress> exchangeTable = (LinkedList<NodeAddress>) in.readObject();
				System.out.println(name + " received table from " + other.getName());
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

	private void printTable(List<NodeAddress> table, boolean ownTable) {
		StringBuilder str;
		if (ownTable) {
			str = new StringBuilder(name + " has table: [");
		} else {
			str = new StringBuilder(name + " got table: [");
		}

		for (NodeAddress nodeAddress : table) {
			str.append(nodeAddress.getName() + ", ");
		}
		int l = str.length();
		str.replace(l - 2, l, "]");
		System.out.println(str);
	}

	public NodeAddress getNodeAddress() {
		return ownAddress;
	}

	public NodeAddress search(List<NodeAddress> trace) {
		String destination = trace.get(0).getName();
		NodeAddress initiator = trace.get(1);

		// If we are the destination: SUCCESS!
		if (name.compareTo(destination) == 0) {
			System.out.println("Found Destination " + destination + "!");
			return ownAddress;
		}

		// Add new request if not already handled
		synchronized (requests) {
			if (requests.containsKey(initiator)) {
				List<String> searchedNames = requests.get(initiator);
				if (searchedNames.contains(destination)) {
					// do nothing
					return null;
				} else {
					// add new request
					searchedNames.add(destination);
					requests.replace(initiator, searchedNames);
				}
			} else {
				List<String> searchedNames = new LinkedList<String>();
				searchedNames.add(destination);
				requests.put(initiator, searchedNames);
			}
		}

		trace.add(ownAddress);

		// Copy table (to avoid later synchronization and deadlock)
		List<NodeAddress> ownTable = new LinkedList<NodeAddress>();
		synchronized (table) {
			ownTable.addAll(table);
		}

		// Check if destination is in table
		for (NodeAddress nodeAddress : ownTable) {
			if (nodeAddress.getName().compareTo(destination) == 0) {
				// Send to this node, success imminent... unless there is a
				// timeout, then FAIL
				return sendSearchRequest(nodeAddress, trace);
			}
		}

		// Destination is not in table... send search request to every known
		// node not in trace
		for (NodeAddress nodeAddress : ownTable) {
			if (!trace.contains(nodeAddress)) {
				NodeAddress result = sendSearchRequest(nodeAddress, trace);
				if (result != null) {
					return result;
				}
			}
		}

		// None of the nodes in table found destination => failed
		return null;
	}

	private NodeAddress sendSearchRequest(NodeAddress other, List<NodeAddress> trace) {
		Socket socket = null;
		NodeAddress result = null;
		try {
			socket = new Socket(other.getAddress(), other.getPort());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			System.out.println(name + " asks " + other.getName());
			out.writeObject(trace);
			out.flush();
			result = (NodeAddress) in.readObject();
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

		return result;
	}

	private class RequestHandler extends Thread {
		private Socket socket;

		public RequestHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			System.out.println(name + " tries to handle request");
			ObjectInputStream in = null;
			ObjectOutputStream out = null;
			try {
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());

				// receive message from connection
				LinkedList<NodeAddress> nodeAddresses = (LinkedList<NodeAddress>) in.readObject();

				// Check if it is a search request
				if (nodeAddresses != null && nodeAddresses.get(0).getSearchQuest()) {
					NodeAddress searchResult = search(nodeAddresses);
					if (searchResult == null) {
						System.out.println(name + " got a search request, but " + nodeAddresses.get(0).getName()
								+ " was not found!");
					} else {
						System.out.println(
								name + " got a search request, and " + nodeAddresses.get(0).getName() + " was found!");
					}
					out.writeObject(searchResult);
				} else { // It is a table exchange request
					synchronized (table) {
						out.writeObject(table);
						mergeTables(nodeAddresses);
					}
				}
			} catch (SocketException e) {
				System.out.println(name + ": Socket closed!");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}

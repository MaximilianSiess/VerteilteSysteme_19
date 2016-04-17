package blatt4;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Node extends Thread {

	private final int n;
	private final String name;
	private final NodeAddress ownAddress;
	private LinkedList<NodeAddress> table;
	private ServerSocket serverSocket;
	private boolean isActive = true;
	private Map<NodeAddress, List<String>> requests = new HashMap<NodeAddress, List<String>>();
	private Map<NodeAddress, List<String>> inbox = new HashMap<NodeAddress, List<String>>();

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
			for (NodeAddress nodeAddress : exchangeTable) {
				if (!(nodeAddress.equals(ownAddress) || table.contains(nodeAddress))) {
					table.add(nodeAddress);
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
		final ExecutorService executor = Executors.newFixedThreadPool(n * 3);
		executor.submit(new Runnable() {

			@Override
			public void run() {
				while (isActive) {
					Socket socket = null;

					try {
						socket = serverSocket.accept();
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
					// pick random node from table for exchange
					NodeAddress other = table.get((int) (Math.random() * (table.size() - 1)));
					exchangeTables(other);
				}
			}
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				isActive = false;
				// stop Node
				try {
					executor.shutdown();
					executor.awaitTermination(1, TimeUnit.SECONDS);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				interrupt();
			}
		}
	}

	private void exchangeTables(NodeAddress other) {
		Socket socket = null;
		try {
			socket = new Socket(other.getAddress(), other.getPort());
			socket.setSoTimeout(1500);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			synchronized (table) {
				table.add(ownAddress);
				out.writeObject(new Message(MessageType.EXCHANGE, table));
				System.out.println(name + " sent table to " + other.getName());
				Message answer = (Message) in.readObject();
				LinkedList<NodeAddress> exchangeTable = answer.getSubject();
				System.out.println(name + " received table from " + other.getName());
				table.remove(ownAddress);
				mergeTables(exchangeTable);
			}
		} catch (SocketTimeoutException e) {
			removeUnavailable(other);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			table.remove(ownAddress);
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

	public NodeAddress search(LinkedList<NodeAddress> trace) {
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
					requests.put(initiator, searchedNames);	
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

	private NodeAddress sendSearchRequest(NodeAddress other, LinkedList<NodeAddress> trace) {
		Socket socket = null;
		NodeAddress result = null;
		try {
			socket = new Socket(other.getAddress(), other.getPort());
			socket.setSoTimeout(1500);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			System.out.println(name + " asks " + other.getName());
			out.writeObject(new Message(MessageType.SEARCH, trace));
			result = (NodeAddress) in.readObject();
		} catch (SocketTimeoutException e) {
			removeUnavailable(other);
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

	public void flood(Message msg) {
		String text = msg.getText();
		LinkedList<NodeAddress> trace = msg.getSubject();
		NodeAddress initiator;
		if (trace.size() > 0) {
			initiator = trace.get(0);
		} else {
			initiator = ownAddress;
		}

		if (!trace.contains(ownAddress)) {

			// Add new msg if not already in inbox
			synchronized (inbox) {
				// Set<NodeAddress> keys = inbox.keySet();
				if (inbox.containsKey(initiator)) {
					List<String> msgs = inbox.get(initiator);
					if (msgs.contains(text)) {
						// do nothing
						return;
					} else {
						// add new request
						msgs.add(text);
						inbox.put(initiator, msgs);
					}
				} else {
					List<String> msgs = new LinkedList<String>();
					msgs.add(text);
					inbox.put(initiator, msgs);
				}
			}
			System.out.println(name + " got: " + text);

			// Copy table (to avoid later synchronization and deadlock)
			List<NodeAddress> ownTable = new LinkedList<NodeAddress>();
			synchronized (table) {
				ownTable.addAll(table);
			}
			trace.add(ownAddress);
			msg.setSubject(trace);

			for (NodeAddress nodeAddress : ownTable) {
				if (!trace.contains(nodeAddress)) {
					Socket socket = null;
					try {
						socket = new Socket(nodeAddress.getAddress(), nodeAddress.getPort());
						socket.setSoTimeout(1500);
						ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
						out.writeObject(msg);
					} catch (SocketTimeoutException e) {
						removeUnavailable(nodeAddress);
					} catch (SocketException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void removeUnavailable(NodeAddress other) {
		synchronized (table) {
			System.out.println(name + ": " + other.getName() + " was not available!");
			table.remove(other);
		}
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
				Message msg = (Message) in.readObject();
				if (msg.getType() == MessageType.EXCHANGE) {
					LinkedList<NodeAddress> exchangeTable = msg.getSubject();
					synchronized (table) {
						out.writeObject(new Message(MessageType.EXCHANGE, table));
						mergeTables(exchangeTable);
					}
				} else if (msg.getType() == MessageType.SEARCH) {
					LinkedList<NodeAddress> trace = msg.getSubject();
					NodeAddress searchResult = search(trace);
					if (searchResult == null) {
						System.out.println(
								name + " got a search request, but " + trace.get(0).getName() + " was not found!");
					} else {
						System.out
								.println(name + " got a search request, and " + trace.get(0).getName() + " was found!");
					}
					out.writeObject(searchResult);
				} else {// type INFO
					flood(msg);
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (SocketTimeoutException e) {
				// other node gave already up
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

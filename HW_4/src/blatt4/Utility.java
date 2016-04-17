package blatt4;

import java.util.LinkedList;

public abstract class Utility {

	public static void removeNode(Node node) {
		node.interrupt();
	}

	public static void addNode(Node node) {
		node.start();
	}

	public static void findNode(Node first, String name) {
		NodeAddress searchitem = new NodeAddress(null, 0, name);
		LinkedList<NodeAddress> trace = new LinkedList<NodeAddress>();
		trace.add(searchitem);
		trace.add(first.getNodeAddress());
		NodeAddress result = first.search(trace);
		if (result != null) {
			System.out.println("Success! Found " + name + "! Address is: " + result.getAddress() + " Port is: "
					+ result.getPort());
		} else {
			System.out.println(("Could not find " + name + "..."));
		}
	}

	public static void sendFloodMessage(Node first, String text) {
		LinkedList<NodeAddress> trace = new LinkedList<NodeAddress>();
		trace.add(first.getNodeAddress());
		Message msg = new Message(text);
		System.out.println("Try to flood: " + text);
		first.flood(msg);
	}

	public static void sleep(int miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

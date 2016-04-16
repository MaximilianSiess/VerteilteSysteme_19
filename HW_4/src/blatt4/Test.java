package blatt4;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class Test {
	public static void main(String[] args) {
		int n = 3;
		int number = 3 * n;
		InetAddress address;
		List<Node> nodes = new LinkedList<Node>();

		try {
			address = InetAddress.getLocalHost();
			nodes.add(new Node("Node0", 2000, address, n));
			System.out.println("Node 0 created!");
			for (int i = 1; i < number; i++) {
				nodes.add(new Node("Node" + i, 2000 + i, InetAddress.getLocalHost(), n, nodes.get(i - 1)));
				System.out.println("Node " + i + " created!");
			}

			for (Node node : nodes) {
				node.start();
			}
			for (int i = number; i < number + n; i++) {
				Utility.sleep(1000);
				Node newnode = new Node("Node" + i, 2000 + i, InetAddress.getLocalHost(), n, nodes.get(0));
				nodes.add(newnode);
				Utility.addNode(newnode);
				System.out.println("Node " + i + " added!");
			}
			
			Utility.sleep(1000);
			//Utility.findNode(nodes.get(0), "Node6");
			//Utility.findNode(nodes.get(0), "IDon'tExist");
			
			System.out.println("Type something to close all " + nodes.size() + " nodes.");
			try {
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (int i = 0; i < nodes.size(); i++) {
				Utility.removeNode(nodes.get(i));
				System.out.println("Node " + i + " removed!");
				Utility.sleep(100);
			}

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		System.out.println("Done! Quitting...");
	}
}

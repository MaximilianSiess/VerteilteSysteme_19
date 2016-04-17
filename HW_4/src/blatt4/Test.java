package blatt4;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class Test {
	public static void main(String[] args) {
		int n = 3;
		int number = 4;
		InetAddress address;
		String name;
		List<Node> nodes = new LinkedList<Node>();

		try {
			address = InetAddress.getLocalHost();
			name = "Node0";
			nodes.add(new Node(name, 2000, address, n));
			System.out.println(name + " created!");
			for (int i = 1; i < number; i++) {
				name = "Node" + i;
				nodes.add(new Node(name, 2000 + i, address, n, nodes.get(i - 1)));
				System.out.println(name + " created!");
			}

			for (Node node : nodes) {
				Utility.addNode(node);
			}

			// add some nodes
			for (int i = number; i < number + n; i++) {
				Utility.sleep(100);
				name = "Node" + i;
				Node newnode = new Node(name, 2000 + i, address, n, nodes.get(0));
				nodes.add(newnode);
				Utility.addNode(newnode);
				System.out.println(name + " added!");
			}

			// search for nodes
			Utility.sleep(1000);
			Utility.findNode(nodes.get(0), "Node2");
			Utility.findNode(nodes.get(0), "IDon'tExist");

			/*
			 * System.out.println("Type something to close all " + nodes.size()
			 * + " nodes."); try { System.in.read(); } catch (IOException e) {
			 * e.printStackTrace(); }
			 * 
			 * for (int i = 0; i < nodes.size(); i++) {
			 * Utility.removeNode(nodes.get(i)); System.out.println("Node " + i
			 * + " removed!"); Utility.sleep(100); }
			 */

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		for (int i = 0; i < nodes.size(); i++) {
			try {
				nodes.get(i).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Node " + i + " joined!");
		}
		System.out.println("Done! Quitting...");
	}
}

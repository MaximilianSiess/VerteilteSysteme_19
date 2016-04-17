package blatt4;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class Test {
	public static void main(String[] args) {
		int n = 3;
		int number = 3 * n;
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

			int rand = (int) (Math.random() * nodes.size());
			Utility.removeNode(nodes.get(rand));
			nodes.remove(rand);
			System.out.println("Node" + rand + " removed!");
			Utility.sleep(100);
			rand = (int) (Math.random() * nodes.size());
			Utility.removeNode(nodes.get(rand));
			nodes.remove(rand);
			System.out.println("Node" + rand + " removed!");

			// flooding (wait until the network is better connected)
			Utility.sleep(15000);
			Utility.sendFloodMessage(nodes.get(0), "To all: Beer for free!");

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
	}
}

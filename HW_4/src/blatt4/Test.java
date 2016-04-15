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
		List<Node> nodes = new LinkedList<Node>();

		try {
			address = InetAddress.getLocalHost();
			nodes.add(new Node(2000, address, n));
			System.out.println("Node 0 created!");
			for (int i = 1; i < number; i++) {
				nodes.add(new Node(2000 + i, InetAddress.getLocalHost(), n, nodes.get(i - 1)));
				System.out.println("Node " + i + " created!");
			}

			for (Node node : nodes) {
				node.start();
			}
			for (int i = number; i < number + n; i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Utility.addNode(new Node(2000 + i, InetAddress.getLocalHost(), n, nodes.get(0)));
				System.out.println("Node " + i + " added!");
			}
			for (int i = 0; i < n; i++) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Utility.removeNode(nodes.get(i * 3));
				System.out.println("Node " + i + " removed!");
			}

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

	}
}

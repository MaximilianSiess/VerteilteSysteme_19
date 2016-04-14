package blatt4;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ExerciseCTest {
	public static void main(String[] args) {
		Utility utility = Utility.getInstance();

		int number = 2;
		int n = 3;

		Node[] nodes = new Node[number];
		try {
			nodes[0] = new Node(2000, InetAddress.getLocalHost(), n);

			for (int i = 1; i < number; i++) {
				nodes[i] = new Node(2000 + i, InetAddress.getLocalHost(), n, nodes[i - 1]);
				System.out.println("Node " + i + " created!");
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int i = 1;
		while (true) { 
			for (; i < nodes.length; i++) {
				// Exchange
				utility.exchange(nodes[i].getTable(), nodes[i]);
				System.out.println("Node " + i + " exchanged!");
			}
			i = 0;
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

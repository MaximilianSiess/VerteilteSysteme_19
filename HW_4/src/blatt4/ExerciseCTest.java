package blatt4;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ExerciseCTest {
	public static void main(String[] args) {
		Utility utility = Utility.getInstance();

		int number = 30;
		int n = 3;

		Node[] nodes = new Node[number];
		try {
			nodes[0] = new Node(0, InetAddress.getLocalHost(), n);

			for (int i = 1; i < number; i++) {
				nodes[i] = new Node(i, InetAddress.getLocalHost(), n, nodes[i - 1]);
			}
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {
			for (int i = 0; i < nodes.length; i++) {

			}
		}
	}
}

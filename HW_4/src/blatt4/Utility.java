package blatt4;

public abstract class Utility {

	public static void removeNode(Node node) {
		node.interrupt();
	}

	public static void addNode(Node node) {
		node.start();
	}
}

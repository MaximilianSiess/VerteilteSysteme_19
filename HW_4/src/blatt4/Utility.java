package blatt4;

public abstract class Utility {

	public static void removeNode(Node node) {
		node.interrupt();
	}

	public static void addNode(Node node) {
		node.start();
	}
	
	public static void findNode(Node first, String name) {
		NodeAddress searchitem = first.search(name);
		if(searchitem != null) {
			System.out.println("Success! Found " + name + "! Address is: " + searchitem.getAddress() + " Port is: " + searchitem.getPort());
		} else {
			System.out.println(("Could not find " + name + "..."));
		}
	}
	
	public static void sleep(int miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

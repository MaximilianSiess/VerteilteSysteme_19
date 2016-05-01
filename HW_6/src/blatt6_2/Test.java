package blatt6_2;

public class Test {
	public static void main(String[] args) {
		int portNumber = 1024;
		Dispatcher<Integer> dispatcher = new Dispatcher<Integer>(portNumber++);
		for (int i = 0; i < 3; i++) {
			Server2<Integer> server = new Server2<Integer>(i, portNumber++, dispatcher);
			server.start();
		}
		for (int i = 0; i < 4; i++) {
			Client2 client = new Client2(dispatcher);
			client.start();
		}
	}
}

package ex3_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerShutdown extends Thread {
	private Server server;
	private boolean closing = false;

	public ServerShutdown(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		
		// Shutdown hook to catch ctrl+C TERM signal
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run(){
				System.out.println("Server: TERM Signal recieved.");
				while (!closing) {
					server.stopServer();
					System.out.println("Start Closing");
					closing = true;
				}
			}
		});
		
		System.out.println("Type 1 to shutdown server.");
		int i = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (!closing) {
			try {
				i = Integer.parseInt(br.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (i == 1) {
				server.stopServer();
				System.out.println("Start Closing");
				closing = true;
			}
		}
	}
}

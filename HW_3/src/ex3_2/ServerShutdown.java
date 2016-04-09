package ex3_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerShutdown extends Thread {
	private ServiceAnnouncer server;
	private boolean closing = false;

	public ServerShutdown(ServiceAnnouncer server) {
		this.server = server;
	}

	@Override
	public void run() {
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
				server.stopServiceAnnouncer();
				System.out.println("Start Closing");
				closing = true;
			}
		}
	}
}

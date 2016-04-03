package ex2_4;

import java.io.IOException;
import java.net.Socket;

import ex2_3.Protocol;

public class HandleRequest extends Thread {
	private Socket connection;

	public HandleRequest(Socket connection) {
		this.connection = connection;
		System.out.println("Connection received from " + connection.getInetAddress().getHostName());
	}

	@Override
	public void run() {
		Protocol protocol = new Protocol();

		try {
			protocol.InitServer(connection);

			boolean socketOpen = true;

			// Go for as long as the client is not closed
			while (socketOpen) {
				socketOpen = protocol.reply();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

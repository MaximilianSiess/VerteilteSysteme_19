package ex2_4;

import java.io.IOException;
import java.net.Socket;

import ex2_3.Protocol;

public class HandleRequest extends Thread {
	private Socket connection;

	public void RequestHandlerThread(Socket connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		try {
			Protocol.reply();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

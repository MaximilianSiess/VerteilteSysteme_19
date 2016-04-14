package blatt4;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Utility {

	private static Utility instance = null;

	public static Utility getInstance() {
		if (instance == null) {
			instance = new Utility();
		}
		return instance;
	}

	private Socket connect(Node self, Node other) {
		Socket socket = null;
		try {
			self.getSocket().bind(new InetSocketAddress(other.getAdress(), other.getPort()));
			socket = other.getServerSocket().accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return socket;
	}

	public void disconnect(Node self) {
		try {
			self.getSocket().close();
			// TODO Serverseite
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void exchange(Node[] table, Node self) {
		// TODO random
		Node other = table[0];
		exchangeHelper(table, self, other);
		exchangeHelper(other.getTable(), other, self);
	}

	public void exchangeHelper(Node[] table, Node self, Node other) {

		Socket socket = connect(self, other);
		try {
			ObjectOutputStream out = new ObjectOutputStream(self.getSocket().getOutputStream());
			out.writeObject(table);
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			try {
				Node[] exchangeTable = (Node[]) in.readObject();
				Node[] newTable = new Node[self.getN()];
				for (int i = 0; i < newTable.length; i++) {
					if (!self.equals(exchangeTable[i]) || exchangeTable[i] == null) {
						newTable[i] = exchangeTable[i];
					} else {
						newTable[i] = self.getTable()[i];
					}
				}
				other.setTable(newTable);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

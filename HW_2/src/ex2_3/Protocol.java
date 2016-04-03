package ex2_3;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class Protocol {
	private static final int PORTNUMBER = 1234;
	private static BufferedReader ServerIn, ClientIn;
	private static PrintWriter ServerOut, ClientOut;

	public void InitClient(Socket socket) throws IOException {
		ClientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		ClientOut = new PrintWriter(socket.getOutputStream(), true);
	}
	
	public void InitServer(Socket socket) throws IOException {
		ServerIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		ServerOut = new PrintWriter(socket.getOutputStream(), true);
	}
	
	public void closeSocket(Socket socket, boolean server) throws IOException {
		socket.close();
	}

	public String request(Operation operation, int[] integers) throws IOException {
		boolean reading = false;
		String readString = null;

		// Build OutputString
		StringBuffer string = new StringBuffer("ServiceName ");
		string.append(operation.name());
		if (integers.length == 1) {
			string.append(" " + integers[0]);
		} else if (integers.length == 2) {
			string.append(" " + integers[0] + " " + integers[1]);
		}

		// Add extra character because of connection checking with in.read()
		string.insert(0, "0");
		
		// Print operation and integer(s) to output
		ClientOut.println(string);

		// Read InputString
		while (!reading) {
			readString = ClientIn.readLine();
			if (readString != null) {
				reading = true;
			}
		}

		return readString;
	}

	public boolean reply() throws IOException {
		String inString = null;
		StringTokenizer stringTokenizer;
		String operation, auth;
		Operation op;
		int result = 0;
		int first;
		int second;

		// Read InputStream
		while (inString == null) {
			if (ServerIn.read() == -1) {
				System.out.println("Client has seized connection.");
				return false; // Client has closed, wait for a new connection!
			}
			inString = ServerIn.readLine();
		}
		stringTokenizer = new StringTokenizer(inString, " ");
		
		auth = stringTokenizer.nextToken();
		
		if (auth.compareTo("ServiceName") != 0) {
			ServerOut.println("Authentication failure");
			return false;
		}
		
		operation = stringTokenizer.nextToken();
		op = Operation.valueOf(operation);

		// First argument is always needed
		first = Integer.parseInt(stringTokenizer.nextToken());

		if (op == Operation.LUCAS) {
			result = lucas(first);
		} else {
			// Binary operations need second number
			second = Integer.parseInt(stringTokenizer.nextToken());
			if (op == Operation.ADDITION) {
				result = first + second;
			} else if (op == Operation.SUBSTRAKTION) {
				result = first - second;
			} else if (op == Operation.MULTIPLIKATION) {
				result = first * second;
			}
		}

		// Print result to output
		System.out.println(result);
		
		// Send result back to client
		ServerOut.println(result);
		
		return true; // Continue with this socket
	}

	public static int getPortNumber() {
		return PORTNUMBER;
	}

	private static int lucas(int n) {
		if (n == 0)
			return 2;
		if (n == 1)
			return 1;
		return lucas(n - 1) + lucas(n - 2);
	}
}

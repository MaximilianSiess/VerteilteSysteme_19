
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class Protocol {
	private static final int PORTNUMBER = 1234;

	// TODO Safe closing of in and out

	public static void closeSocket(Socket socket) throws IOException {
		socket.close();
	}

	public static int request(Socket socket, Operation operation, int[] integers) throws IOException {
		int result = 0;
		boolean reading = false;
		String readString;

		BufferedReader in;
		PrintWriter out;

		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		out = new PrintWriter(socket.getOutputStream(), true);

		// Build OutputString
		StringBuffer string = new StringBuffer(operation.name());
		if (integers.length == 1) {
			string.append(" " + integers[0]);
		} else if (integers.length == 2) {
			string.append(" " + integers[0] + " " + integers[1]);
		}

		// Print operation and integer(s) to output
		out.println(string);

		// Read InputString
		while (!reading) {
			readString = in.readLine();
			if (readString != null) {
				reading = true;
				result = Integer.parseInt(readString);
			}
		}

		in.close();
		out.close();

		return result;
	}

	public static void reply(Socket socket) throws IOException {
		BufferedReader in;
		PrintWriter out;
		String inString;
		StringTokenizer stringTokenizer;
		String operation;
		Operation op;
		int result = 0;
		int first;
		int second;

		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);

		// Read InputStream
		inString = in.readLine();
		stringTokenizer = new StringTokenizer(inString, " ");
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
		out.println(result);

		in.close();
		out.close();
		socket.close();
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

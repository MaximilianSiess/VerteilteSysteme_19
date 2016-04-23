package ex2_4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ex2_3.Protocol;

public class MultiServer {

	// TODO: ExecutorService only works for one thread at a time...

	private static ServerSocket providerSocket;
	private static LinkedBlockingQueue<Runnable> runnables = new LinkedBlockingQueue<Runnable>();
	private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 100, 100, TimeUnit.MILLISECONDS,
			runnables);

	public static void main(String[] args) {

		// Add a hook for shutdown exception handling
		// NOTE: Hook does not work inside Eclipse
		// must send a TERM signal with kill
		//
		// Runtime.getRuntime().addShutdownHook(new Thread() {
		//
		// @Override public void run() { System.out.println(
		// "Shutdown hook activated!"); System.out.println(
		// "Shutting Server down.."); try { connection.close(); } catch
		// (IOException e) { System.out.println("Could not close server socket."
		// ); e.printStackTrace(); } catch (NullPointerException e) {
		// System.out.println("No connections found."); } finally {
		// executor.shutdown(); // Wait for all threads to finish while
		// (!executor.isTerminated()) { System.out.print("."); } } } });

		// Server logic
		try {
			providerSocket = new ServerSocket();
			providerSocket.setReuseAddress(true);
			providerSocket.bind(new InetSocketAddress(Protocol.getPortNumber()), 10);

			while (true) {
				System.out.println("Waiting for connections...");

				executor.execute(new HandleRequest(providerSocket.accept()));
			}
		} catch (IOException e) {
			System.out.println("Could not establish a connection!");
			e.printStackTrace();
		}
	}

}

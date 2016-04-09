package ex3_2;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestingClass {

	public static void main(String[] args) {
		ExampleServiceAnnouncer announcer;
		ExampleServiceLocator locator;
		int portnumber = 8345;
		
		try {
			
			System.out.println("Creating Server...");
			locator = new ExampleServiceLocator(portnumber);
			
			new Thread(locator).start();
			
			Thread.sleep(1000);
			
			System.out.println("Creating Client...");
			announcer = new ExampleServiceAnnouncer(portnumber);
			
			new Thread(announcer).start();
		} catch (InterruptedException e) {
			System.out.println("Sleep was interrupted:");
			e.printStackTrace();
		}
	}
}
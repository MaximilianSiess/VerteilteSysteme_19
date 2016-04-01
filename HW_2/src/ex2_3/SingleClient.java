package ex2_3;

import java.io.*;
import java.net.*;

import java.util.InputMismatchException;
import java.util.Scanner;

public class SingleClient {
	
	private static int arguments[];
	private static Operation operator;
	
	private static Socket requestSocket;
	
	SingleClient () {
		arguments = new int[2];
	}

	private boolean inputValues() {
		boolean done = false;
		
		while (!done) {
			System.out.println("1)+ 2)- 3)* 4)lucas 5)EXIT");
			System.out.print(">");
			
			try {
				Scanner in = new Scanner(System.in);
				int input = in.nextInt();
			
				switch(input) {
				case 1:
					done = true;
					operator = Operation.ADDITION;
					break;
				case 2:
					done = true;
					operator = Operation.SUBSTRAKTION;
					break;
				case 3:
					done = true;
					operator = Operation.MULTIPLIKATION;
					break;
				case 4:
					done = true;
					operator = Operation.LUCAS;
					break;
				case 5:
					in.close();
					return false;
				default:
					System.out.println("Not a valid input\n\n");
			}
				
			} catch (InputMismatchException e) {
				System.out.println("Input is not an Integer!\n\n");
			}
		}
		
		done = false;
		
		while (!done) {
			System.out.println("Enter the first argument.");
			System.out.print(">");
			
			try {
				Scanner in = new Scanner(System.in);
				int input = in.nextInt();
				arguments[0] = input;
				done = true;
			} catch (InputMismatchException e) {
				System.out.println("Input is not an Integer!\n\n");
			}
		}
		
		// Skip if operator is unary
		if (operator != Operation.LUCAS) {
			done = false;
			
			while (!done) {
				System.out.println("Enter the second argument.");
				System.out.print(">");
				
				try {
					Scanner in = new Scanner(System.in);
					int input = in.nextInt();
					arguments[1] = input;
					done = true;
				} catch (InputMismatchException e) {
					System.out.println("Input is not an Integer!\n\n");
				}
			}
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		SingleClient client = new SingleClient();
		
		System.out.println("*****************CLIENT**************");
		
		try {
			
			// Establish connection
			requestSocket = new Socket("localhost", Protocol.getPortNumber());
	        System.out.println("Connected to localhost in port " + Protocol.getPortNumber());
	        
	        Protocol.InitClient(requestSocket);
			
	        // Get input and hand it to protocol, as often as the user wants
			while (client.inputValues()) {
				String result = Protocol.request(operator, arguments);
				System.out.println("Answer: " + result);
			}
			
			Protocol.closeSocket(requestSocket, false);
			
		} catch (UnknownHostException e) {
			System.out.println("Could not resolve host!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not establish connection!");
			e.printStackTrace();
		}
		
		// We are done here
		System.out.println("Shutting down...");
	}

}

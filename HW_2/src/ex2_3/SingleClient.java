package ex2_3;

import java.io.*;
import java.net.*;

import java.util.InputMismatchException;
import java.util.Scanner;

public class SingleClient {
	
	private int argument1, argument2;
	private String operator;
	
	private Socket requestSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String result = "";

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
					operator = "+";
					break;
				case 2:
					done = true;
					operator = "-";
					break;
				case 3:
					done = true;
					operator = "*";
					break;
				case 4:
					done = true;
					operator = "lucas";
					break;
				case 5:
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
				argument1 = input;
				done = true;
			} catch (InputMismatchException e) {
				System.out.println("Input is not an Integer!\n\n");
			}
		}
		
		// Skip if operator is unary
		if (operator != "lucas") {
			done = false;
			
			while (!done) {
				System.out.println("Enter the second argument.");
				System.out.print(">");
				
				try {
					Scanner in = new Scanner(System.in);
					int input = in.nextInt();
					argument2 = input;
					done = true;
				} catch (InputMismatchException e) {
					System.out.println("Input is not an Integer!\n\n");
				}
			}
		}
		
		return true;
	}
	
	private void attemptConnection() {
		/*
		try {
	        requestSocket = new Socket("localhost", 2004);
	        System.out.println("Connected to localhost in port 2004");
	        
	        out = new ObjectOutputStream(requestSocket.getOutputStream());
	        out.flush();
	        in = new ObjectInputStream(requestSocket.getInputStream());
	        
	        // Write request to server
	        String request = "{ \"service\": \"" + operator + "\","
	        					+ " \"a1\": \"" + argument1 + "\","
	        					+ " \"a2\": \"" + argument2 + "\","
	        						+ " \"name\": \"ourservice\"}";
	        out.writeObject(request);
	        
	        // Wait for result
	        while(result.equals("")) {
	        		try {
	        			result = (String)in.readObject();
	        		} catch (ClassNotFoundException e) {
	        			System.err.println("data received in unknown format");
	        		}
            }
	        
	        System.out.println("Answer: " + result);
	        
		} catch(UnknownHostException unknownHost){
            System.err.println("You are trying to connect to an unknown host!");
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            //4: Closing connection
            try{
                in.close();
                out.close();
                requestSocket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
        */
	}
	
	public static void main(String[] args) {
		SingleClient client = new SingleClient();
		
		System.out.println("*****************CLIENT**************");
		
		while (client.inputValues()) {
			client.attemptConnection();
		}
		
		System.out.println("Shutting down...");
	}

}

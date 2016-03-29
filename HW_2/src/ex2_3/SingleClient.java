package ex2_3;

import org.json.*;
import java.io.*;
import java.net.*;

import java.util.InputMismatchException;
import java.util.Scanner;

public class SingleClient {
	
	private int argument1, argument2;
	private String operator;

	private void inputValues() {
		boolean done = false;
		
		while (!done) {
			System.out.println("1)+ 2)- 3)* 4)lucas");
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
	}
	
	public static void main(String[] args) {
		SingleClient client = new SingleClient();
		
		System.out.println("*****************CLIENT**************");
		
		client.inputValues();
		
		System.out.println("Shutting down...");
	}

}

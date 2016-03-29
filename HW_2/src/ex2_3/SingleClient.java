package ex2_3;

import java.util.InputMismatchException;
import java.util.Scanner;

public class SingleClient {

	private static void inputValues() {
		boolean done = false;
		while (!done) {
			System.out.println("1)+ 2)- 3)* 4)lucas");
			System.out.println(">");
			
			Scanner in = new Scanner(System.in);
			try {
				int input = in.nextInt();
			
				switch(input) {
				case 1:
					done = true;
					break;
				case 2:
					done = true;
					break;
				case 3:
					done = true;
					break;
				case 4:
					done = true;
					break;
				default:
					System.out.println("Not a valid input\n\n");
			}
			
			} catch (InputMismatchException e) {
				System.out.println("Input is not an Integer!\n\n");
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println("*****************CLIENT**************");
		inputValues();
		
		
		System.out.println("Shutting down...");
	}

}

package pt.tecnico.sauron.eye;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class EyeApp {

	public static void main(String[] args) {
		System.out.println(EyeApp.class.getSimpleName());
		
		// receive and print arguments

		if (args.length != 6) {
			System.out.printf("Invalid arguments count: %d", args.length);
			System.exit(1);
		}

		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		System.out.printf("Server address: %s\n", args[0]);
		System.out.printf("Server port: %s\n", args[1]);
		System.out.printf("Camera name: %s\n", args[2]);
		System.out.printf("Coordinates latitude: %f\n", args[3]);
		System.out.printf("Coordinates longitude: %f\n", args[4]);

		Scanner scanner = new Scanner(System.in);
		System.out.printf("> ");
		String input = scanner.nextLine();

		while (!input.equals("exit")) {
			List <String> tokens = new ArrayList<String>(Arrays.asList(input.split(",")));
			if (input.startsWith("#")){
				System.out.print("> ");
				input = scanner.nextLine();
				continue;
			}

			String firstWord = tokens.remove(0);
			if (firstWord.equalsIgnoreCase("person")){
				String identifierPerson = tokens.remove(0);
			}	
			
			if (firstWord.equalsIgnoreCase("car")){
				String identifierCar = tokens.remove(0);
			}

			if (firstWord.equalsIgnoreCase("zzz")){
				Long identifierZzz = Long.parseLong(tokens.remove(0));
				try{
					java.util.concurrent.TimeUnit.MILLISECONDS.sleep(identifierZzz);
				}catch( InterruptedException e ){
					
				}
			}

			System.out.print("> ");
			input = scanner.nextLine();
		}

		scanner.close();
	}

	
}

package pt.tecnico.sauron.spotter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SpotterApp {


	public static void main(String[] args) {
		System.out.println(SpotterApp.class.getSimpleName());

		if (args.length != 2) {
			System.out.printf("Invalid arguments count: %d", args.length);
			System.exit(1);
		}

		// TODO: connect with silo-client
		System.out.printf("Server address: %s\n", args[0]);
		System.out.printf("Server port: %s\n", args[1]);

		Scanner scanner = new Scanner(System.in);
		System.out.printf("> ");
		String input = scanner.nextLine();

		while (!input.equals("exit")) {
			List<String> tokens = new ArrayList<String>(Arrays.asList(input.split(" ")));
			if (tokens.size() < 1) {
				System.out.println("Invalid input.");
				break;
			}

			String command = tokens.remove(0);

			switch (command) {
				case "help":
					help(tokens);
					break;
				case "ping":
					ping(tokens);
					break;
				case "clear":
					clear(tokens);
					break;
				case "trail":
					trail(tokens);
					break;
				case "spot":
					spot(tokens);
					break;
				default:
					System.out.printf("Invalid command: %s\n", input);
					break;
			}

			System.out.print("> ");
			input = scanner.nextLine();
		}

		scanner.close();
	}

	private static void help(List<String> tokens) {
		System.out.println("Commands:");
		System.out.println("\thelp - this command");
		System.out.println("\texit - stop spotter");
		System.out.println("\tspot type identifier - fetch last observation");
		System.out.println("\ttrail type identifier - fetch observations history");
		System.out.println("\nControl commands:");
		System.out.println("\tping - ping the server for status");
		System.out.print("\tclear - clear the server");
	}

	private static void ping(List<String> tokens) {
		if (tokens.size() != 0) {
			System.out.printf("Invalid arguments: %s. Run 'help' for more information.\n", String.join(" ", tokens));
			return;
		}

	}

	private static void clear(List<String> tokens) {
		if (tokens.size() != 0) {
			System.out.printf("Invalid arguments: %s. Run 'help' for more information.\n", String.join(" ", tokens));
			return;
		}

	}

	private static Boolean validateTrailSpot(List<String> tokens) {
		List<String> supportedTypes = List.of("person", "car");

		if (tokens.size() != 2) {
			System.out.printf("Invalid arguments: %s. Run 'help' for more information.\n", String.join(" ", tokens));
			return false;
		}

		if (!supportedTypes.contains(tokens.get(0))) {
			System.out.printf("Object type of %s is unknown.\n", tokens.get(0));
			return false;
		}

		return true;
	}

	private static void trail(List<String> tokens) {
		if (!validateTrailSpot(tokens)) {
			return;
		}

		String type = tokens.get(0);
		String identifier = tokens.get(1);

		if (identifier.contains("*")) {
			System.out.print("Trail identifiers must be exact.\n");
			return;
		}

		// TODO
	}

	private static void spot(List<String> tokens) {
		if (!validateTrailSpot(tokens)) {
			return;
		}

		String type = tokens.get(0);
		String identifier = tokens.get(1);

		if (identifier.contains("*")) {
			// MATCH
		}

		// TODO
	}

	private static void printFormatted(List<Object> observations) {
		// TODO
		// Sorted ASC identificador

		for (Object observation: observations) {
			// Tipo,Identificador,Data-Hora,Nome-Câmera,Latitude-Câmera,Longitude-Câmera
			// Datetime 8601 c/ segundos
		}
	}
}

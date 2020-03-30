package pt.tecnico.sauron.spotter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import pt.tecnico.sauron.silo.client.domain.Observation;
import pt.tecnico.sauron.silo.client.domain.ObservationType;
import pt.tecnico.sauron.silo.client.domain.SiloFrontend;
import pt.tecnico.sauron.silo.client.exceptions.SauronClientException;

public class SpotterApp {
	private static Map<String, ObservationType> typesConverter = Map.ofEntries(
		Map.entry("person", ObservationType.PERSON),
		Map.entry("car", ObservationType.CAR));

	public static void main(String[] args) {
		System.out.println(SpotterApp.class.getSimpleName());

		if (args.length != 2) {
			System.out.printf("Invalid arguments count: %d", args.length);
			System.exit(1);
		}

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
		final SiloFrontend api = new SiloFrontend(host, port);

		System.out.printf("Server address: %s\n", host);
		System.out.printf("Server port: %s\n", port);

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

			try {
				switch (command) {
					case "help":
						help(tokens, api);
						break;
					case "ping":
						ping(tokens, api);
						break;
					case "clear":
						clear(tokens, api);
						break;
					case "trail":
						trail(tokens, api);
						break;
					case "spot":
						spot(tokens, api);
						break;
					default:
						System.out.printf("Invalid command: %s\n", input);
						break;
				}
			} catch (SauronClientException e) {
				System.out.println(e);
			}

			System.out.print("> ");
			input = scanner.nextLine();
		}

		scanner.close();
	}

	private static void help(List<String> tokens, SiloFrontend api) {
		System.out.println("Commands:");
		System.out.println("\thelp - this command");
		System.out.println("\texit - stop spotter");
		System.out.println("\tspot type identifier - fetch last observation");
		System.out.println("\ttrail type identifier - fetch observations history");
		System.out.println("\nControl commands:");
		System.out.println("\tping - ping the server for status");
		System.out.println("\tclear - clear the server");
	}

	private static void ping(List<String> tokens, SiloFrontend api) throws SauronClientException {
		if (tokens.size() != 0) {
			System.out.printf("Invalid arguments: %s. Run 'help' for more information.\n", String.join(" ", tokens));
			return;
		}

		System.out.println(api.ping());
	}

	private static void clear(List<String> tokens, SiloFrontend api) throws SauronClientException {
		if (tokens.size() != 0) {
			System.out.printf("Invalid arguments: %s. Run 'help' for more information.\n", String.join(" ", tokens));
			return;
		}

		api.clear();
		System.out.println("Server cleared.");
	}

	private static Boolean validateTrailSpot(List<String> tokens) {
		if (tokens.size() != 2) {
			System.out.printf("Invalid arguments: %s. Run 'help' for more information.\n", String.join(" ", tokens));
			return false;
		}

		if (typesConverter.get(tokens.get(0).toLowerCase()) == null) {
			System.out.printf("Object type of %s is unknown.\n", tokens.get(0));
			return false;
		}

		return true;
	}

	private static void trail(List<String> tokens, SiloFrontend api) throws SauronClientException {
		if (!validateTrailSpot(tokens)) {
			return;
		}

		ObservationType type = typesConverter.get(tokens.get(0).toLowerCase());
		String identifier = tokens.get(1);

		if (identifier.contains("*")) {
			System.out.print("Trail identifiers must be exact.\n");
			return;
		}


		List<Observation> observations = api.trace(type, identifier);
		printFormatted(observations);
	}

	private static void spot(List<String> tokens, SiloFrontend api) throws SauronClientException {
		if (!validateTrailSpot(tokens)) {
			return;
		}

		ObservationType type = typesConverter.get(tokens.get(0).toLowerCase());
		String identifier = tokens.get(1);
		List<Observation> observations;

		if (identifier.contains("*")) {
			observations = api.trackMatch(type, identifier);
		} else {
			observations = List.of(api.track(type, identifier));
		}

		printFormatted(observations);
	}

	private static void printFormatted(List<Observation> observations) {
		observations.sort(new Observation.IdentifierComparator());

		for (Observation observation: observations) {
			System.out.printf(
				"%s,%s,%s,%s,%f,%f\n",
				observation.getType(),
				observation.getIdentifier(),
				observation.getDatetime().format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss")),
				observation.getCamera().getName(),
				observation.getCamera().getCoordinates().getLatitude(),
				observation.getCamera().getCoordinates().getLongitude()
			);
		}
	}
}

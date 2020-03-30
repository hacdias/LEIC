package pt.tecnico.sauron.silo.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import pt.tecnico.sauron.silo.client.domain.SiloFrontend;
import pt.tecnico.sauron.silo.client.domain.Status;
import pt.tecnico.sauron.silo.client.exceptions.SauronClientException;

public class SiloClientApp {
	public static void main(String[] args) {
		System.out.println(SiloClientApp.class.getSimpleName());

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
		final SiloFrontend frontend = new SiloFrontend(host, port);

		Scanner scanner = new Scanner(System.in);
		System.out.printf("> ");
		System.out.flush();
		String input = scanner.nextLine();

		while (!input.equals("exit")) {
			List<String> tokens = new ArrayList<String>(Arrays.asList(input.split(" ")));
			String command = tokens.remove(0);

			try {
				switch (command) {
					case "help":
						help(tokens);
						break;
					case "ping":
						ping(frontend, tokens);
						break;
					case "init":
						init(frontend, tokens);
					case "clear":
						clear(frontend, tokens);
						break;
					default:
						System.out.printf("Invalid command: %s\n", input);
						break;
				}
			} catch (SauronClientException e) {
				System.out.println(e);
			}

			System.out.print("> ");
			System.out.flush();
			input = scanner.nextLine();
		}

		scanner.close();
	}

	private static void help(List<String> tokens) {
		System.out.println("Commands:");
		System.out.println("\thelp - this command");
		System.out.println("\texit - stop SILO-CLIENT");
		System.out.println("\nControl commands:");
		System.out.println("\tping - ping the server for status");
		System.out.println("\tinit - initialize the server with a state");
		System.out.println("\tclear - clear the server");
	}

	private static void ping(SiloFrontend frontend, List<String> tokens) throws SauronClientException {
		Status status = frontend.ping();
		System.out.println(status);
	}

	private static void init(SiloFrontend frontend, List<String> tokens) throws SauronClientException {
		frontend.init();
	}

	private static void clear(SiloFrontend frontend, List<String> tokens) throws SauronClientException {
		frontend.clear();
	}
}

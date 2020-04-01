package pt.tecnico.sauron.eye;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import pt.tecnico.sauron.silo.client.domain.Observation;
import pt.tecnico.sauron.silo.client.domain.Camera;
import pt.tecnico.sauron.silo.client.domain.Coordinates;
import pt.tecnico.sauron.silo.client.domain.ObservationType;
import pt.tecnico.sauron.silo.client.domain.SiloFrontend;
import pt.tecnico.sauron.silo.client.exceptions.SauronClientException;


public class EyeApp {

	private static Map<String, ObservationType> typesConverter = Map.ofEntries(
		Map.entry("person", ObservationType.PERSON),
		Map.entry("car", ObservationType.CAR));

	public static void main(String[] args) {
		System.out.println(EyeApp.class.getSimpleName());

		final List<Observation> observations = new ArrayList<Observation>();
		final Camera camera;

		// receive and print arguments

		if (args.length != 5) {
			System.out.printf("Invalid arguments count: %d", args.length);
			System.exit(1);
		}

		final String host = args[0];
		final Integer port = Integer.parseInt(args[1]);
		final String cameraName = args[2];
		final Double coordinatesLatitude = Double.parseDouble(args[3]);
		final Double coordinatesLongitude = Double.parseDouble(args[4]);
	
		final SiloFrontend api = new SiloFrontend(host, port);
		try {
			api.camJoin(cameraName, coordinatesLatitude, coordinatesLongitude);
		} catch(SauronClientException e){
			System.out.println(e);
			api.close();
			System.exit(1);
		}

		Coordinates coordinates = new Coordinates(coordinatesLatitude, coordinatesLongitude);
		camera = new Camera(cameraName, coordinates);
		
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();

		while (!input.equals("exit")) {
			List <String> tokens = new ArrayList<String>(Arrays.asList(input.split(",")));
			if (input.startsWith("#")){
				input = scanner.nextLine();
				continue;
			}

			String firstWord = tokens.remove(0).toLowerCase();
			if (firstWord.equals("person") || firstWord.equals("car")){
				String identifier = tokens.remove(0);
				observations.add(createObservation(camera, firstWord, identifier));
			}

			else if (input.isBlank()){
				try {
					api.report(camera.getName(), observations);
				} catch (SauronClientException e) {
					System.out.println(e);
				}
				observations.clear();
			}

			else if (firstWord.equals("zzz")){
				Long identifierZzz = Long.parseLong(tokens.remove(0));
				try{
					java.util.concurrent.TimeUnit.MILLISECONDS.sleep(identifierZzz);
				}catch( InterruptedException e ){

				}
			}

			else {
				System.out.println("Invalid input");
			}
			input = scanner.nextLine();
		}

		scanner.close();
		try {
			api.report(camera.getName(), observations);
		} catch (SauronClientException e) {
			System.out.println(e);
		}
		observations.clear();
		api.close();
	}

	public static Observation createObservation(Camera camera, String type, String identifier) {
		ObservationType observationType = typesConverter.get(type);
		Observation observation = new Observation(camera, observationType, identifier, LocalDateTime.now());	
		return observation;	
	}
}

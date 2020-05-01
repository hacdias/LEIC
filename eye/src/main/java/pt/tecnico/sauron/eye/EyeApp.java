package pt.tecnico.sauron.eye;

import pt.tecnico.sauron.silo.client.domain.*;
import pt.tecnico.sauron.silo.client.exceptions.SauronClientException;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.time.LocalDateTime;
import java.util.*;

public class EyeApp {
    private static Map<String, ObservationType> typesConverter = Map.ofEntries(
        Map.entry("person", ObservationType.PERSON),
        Map.entry("car", ObservationType.CAR)
    );

    public static void main(String[] args) {
        System.out.println(EyeApp.class.getSimpleName());

        final List<Observation> observations = new ArrayList<>();
        final Camera camera;

        if (args.length < 5 || args.length > 6) {
            System.out.printf("Invalid arguments count: %d\n", args.length);
            System.exit(1);
        }

        final String zooHost = args[0];
        final Integer zooPort = Integer.parseInt(args[1]);

        final String cameraName = args[2];
        final Double coordinatesLatitude = Double.parseDouble(args[3]);
        final Double coordinatesLongitude = Double.parseDouble(args[4]);

        int instance = -1;
        if (args.length == 6)
            instance = Integer.parseInt(args[5]);

        try (SiloFrontend api = new SiloFrontend(zooHost, zooPort, instance)) {
            try {
                System.out.print("Registering camera...");
                api.camJoin(cameraName, coordinatesLatitude, coordinatesLongitude);
                System.out.println(" done");
            } catch (SauronClientException e) {
                System.err.println(e.toString());
                api.close();
                System.exit(1);
            }

            camera = new Camera(cameraName, coordinatesLatitude, coordinatesLongitude);

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            while (input != null && !input.equals("exit")) {
                List<String> tokens = new ArrayList<>(Arrays.asList(input.split(",")));
                if (input.startsWith("#")) {
                    input = scanner.nextLine();
                    continue;
                }

                String firstWord = tokens.remove(0).toLowerCase();

                if (firstWord.equals("person") || firstWord.equals("car")) {
                    String identifier = tokens.remove(0);
                    observations.add(createObservation(camera, firstWord, identifier));
                } else if (input.isBlank()) {
                    try {
                        api.report(camera.getName(), observations);
                    } catch (SauronClientException e) {
                        System.err.println(e.toString());
                    }
                    observations.clear();
                } else if (firstWord.equals("zzz")) {
                    long identifierZzz = Long.parseLong(tokens.remove(0));
                    try {
                        Thread.sleep(identifierZzz);
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    System.out.println("Invalid input");
                }

                if (scanner.hasNextLine()) input = scanner.nextLine();
                else input = null;
            }

            scanner.close();
            try {
                api.report(camera.getName(), observations);
            } catch (SauronClientException e) {
                System.out.println(e);
            }
            observations.clear();
        } catch (ZKNamingException | SauronClientException e) {
            System.err.println("Could not connect to ZooKeeper");
            System.err.print(e.toString());
            System.exit(1);
        }
    }

    public static Observation createObservation(Camera camera, String type, String identifier) {
        ObservationType observationType = typesConverter.get(type);
        return new Observation(camera, observationType, identifier, LocalDateTime.now());
    }
}

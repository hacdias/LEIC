package pt.tecnico.sauron.spotter;

import pt.tecnico.sauron.silo.client.domain.Observation;
import pt.tecnico.sauron.silo.client.domain.ObservationType;
import pt.tecnico.sauron.silo.client.domain.SiloFrontend;
import pt.tecnico.sauron.silo.client.exceptions.SauronClientException;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class SpotterApp {
    private static Map<String, ObservationType> typesConverter = Map.ofEntries(
        Map.entry("person", ObservationType.PERSON),
        Map.entry("car", ObservationType.CAR));

    public static void main(String[] args) {
        System.out.println(SpotterApp.class.getSimpleName());

        if (args.length < 2 || args.length > 3) {
            System.out.printf("Invalid arguments count: %d", args.length);
            System.exit(1);
        }

        final String zooHost = args[0];
        final Integer zooPort = Integer.parseInt(args[1]);
        int instance = -1;

        if (args.length == 3)
            instance = Integer.parseInt(args[2]);

        Scanner scanner = null;
        try (SiloFrontend api = new SiloFrontend(zooHost, zooPort, instance)) {
            System.out.printf("Server address: %s%n", zooHost);
            System.out.printf("Server port: %s%n", zooPort);

            scanner = new Scanner(System.in);
            System.out.printf("> ");
            String input = scanner.nextLine();

            while (!input.equals("exit")) {
                List<String> tokens = new ArrayList<>(Arrays.asList(input.split(" ")));
                if (tokens.isEmpty()) {
                    System.out.println("Invalid input.");
                    break;
                }

                String command = tokens.remove(0);

                try {
                    switch (command) {
                        case "help":
                            help(api);
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
                            System.out.printf("Invalid command: %s%n", input);
                            break;
                    }
                } catch (SauronClientException e) {
                    System.out.println(e);
                }

                System.out.print("> ");
                input = scanner.nextLine();
            }
        } catch (ZKNamingException e) {
            System.err.println("Could not connect to ZooKeeper");
            System.err.print(e.toString());
            System.exit(1);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static void help(SiloFrontend api) {
        System.out.println("Commands:");
        System.out.println("\thelp - this command");
        System.out.println("\texit - stop spotter");
        System.out.println("\tspot type identifier - fetch last observation");
        System.out.println("\ttrail type identifier - fetch observations history");
        System.out.println("\nControl commands:");
        System.out.println("\tping - ping the server for status");
        System.out.println("\tclear - clear the server");
    }

    private static void ping(List<String> tokens, SiloFrontend api) throws SauronClientException, ZKNamingException {
        if (!tokens.isEmpty()) {
            System.out.printf("Invalid arguments: %s. Run 'help' for more information.%n", String.join(" ", tokens));
            return;
        }

        System.out.println(api.ping());
    }

    private static void clear(List<String> tokens, SiloFrontend api) throws SauronClientException, ZKNamingException {
        if (!tokens.isEmpty()) {
            System.out.printf("Invalid arguments: %s. Run 'help' for more information.%n", String.join(" ", tokens));
            return;
        }

        api.clear();
        System.out.println("Server cleared.");
    }

    private static boolean validateTrailSpot(List<String> tokens) {
        if (tokens.size() != 2) {
            System.out.printf("Invalid arguments: %s. Run 'help' for more information.%n", String.join(" ", tokens));
            return false;
        }

        if (typesConverter.get(tokens.get(0).toLowerCase()) == null) {
            System.out.printf("Object type of %s is unknown.%n", tokens.get(0));
            return false;
        }

        return true;
    }

    private static void trail(List<String> tokens, SiloFrontend api) throws SauronClientException, ZKNamingException {
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

    private static void spot(List<String> tokens, SiloFrontend api) throws SauronClientException, ZKNamingException {
        if (!validateTrailSpot(tokens)) {
            return;
        }

        ObservationType type = typesConverter.get(tokens.get(0).toLowerCase());
        String identifier = tokens.get(1);
        List<Observation> observations;

        if (identifier.contains("*")) {
            observations = api.trackMatch(type, identifier);
        } else {
            observations = new ArrayList<>();
            observations.add(api.track(type, identifier));
        }

        printFormatted(observations);
    }

    private static void printFormatted(List<Observation> observations) {
        observations.sort(new Observation.IdentifierComparator());

        for (Observation observation : observations) {
            System.out.printf(
                "%s,%s,%s,%s,%f,%f%n",
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

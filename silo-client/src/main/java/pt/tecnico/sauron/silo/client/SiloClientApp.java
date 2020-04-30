package pt.tecnico.sauron.silo.client;

import pt.tecnico.sauron.silo.client.domain.SiloFrontend;
import pt.tecnico.sauron.silo.client.domain.Status;
import pt.tecnico.sauron.silo.client.exceptions.SauronClientException;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SiloClientApp {
    public static void main(String[] args) {
        System.out.println(SiloClientApp.class.getSimpleName());

        final String host = args[0];
        final Integer port = Integer.parseInt(args[1]);
        int instance = -1;

        if (args.length == 3)
            instance = Integer.parseInt(args[2]);

        Scanner scanner = null;
        try (SiloFrontend frontend = new SiloFrontend(host, port, instance)) {

            scanner = new Scanner(System.in);
            System.out.printf("> ");
            System.out.flush();
            String input = scanner.nextLine();

            while (!input.equals("exit")) {
                List<String> tokens = new ArrayList<>(Arrays.asList(input.split(" ")));
                String command = tokens.remove(0);

                try {
                    switch (command) {
                        case "help":
                            help();
                            break;
                        case "ping":
                            ping(frontend);
                            break;
                        case "init":
                            init(frontend);
                            break;
                        case "clear":
                            clear(frontend);
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

    private static void help() {
        System.out.println("Commands:");
        System.out.println("\thelp - this command");
        System.out.println("\texit - stop SILO-CLIENT");
        System.out.println("\nControl commands:");
        System.out.println("\tping - ping the server for status");
        System.out.println("\tinit - initialize the server with a state");
        System.out.println("\tclear - clear the server");
    }

    private static void ping(SiloFrontend frontend) throws SauronClientException, ZKNamingException {
        Status status = frontend.ping();
        System.out.println(status);
    }

    private static void init(SiloFrontend frontend) {
        frontend.init();
    }

    private static void clear(SiloFrontend frontend) throws SauronClientException, ZKNamingException {
        frontend.clear();
    }
}

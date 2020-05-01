package pt.tecnico.sauron.silo;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.tecnico.sauron.silo.domain.Options;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.io.IOException;

public class SiloServerApp {

    public static void main(String[] args) throws IOException, InterruptedException {
        // receive and print arguments
        System.out.printf("Received %d arguments%n", args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.printf("arg[%d] = %s%n", i, args[i]);
        }

        final String zooHost = args[0];
        final String zooPort = args[1];
        final int instance = Integer.parseInt(args[2]);
        final String host = args[3];
        final int basePort = Integer.parseInt(args[4]);
        final String port = Integer.toString(basePort + instance);
        final String path = args[5];
        final Integer numberServers = Integer.parseInt(args[6]);

        final int updateFrequency = 1000 * 30; // TODO: make customizable.
        final String storageFile = "/not/used/yet";

        Options options = new Options(instance, numberServers, host, basePort, storageFile, updateFrequency);

        try {
            System.out.print("Connecting to zookeeper...");
            final ZKNaming zkNaming = new ZKNaming(zooHost, zooPort);
            zkNaming.rebind(path, host, port);
            System.out.println(" connected.");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    zkNaming.unbind(path,host,port);
                    System.out.println("Port unbound.");
                } catch (ZKNamingException e) {
                    System.err.println("There was an error with ZKNamingException: " + e);
                }
            }));

            final BindableService impl = new SiloServiceImpl(options);

            System.out.print("Server starting...");
            Server server = ServerBuilder.forPort(Integer.parseInt(port)).addService(impl).build();
            server.start();
            System.out.println(" started.");

            // Do not exit the main thread. Wait until server is terminated.
            server.awaitTermination();
        } catch (ZKNamingException e) {
            System.err.println("There was an error with ZKNamingException: " + e);
        }
    }

}
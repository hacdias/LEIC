package pt.tecnico.sauron.silo;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

public class SiloServerApp {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		ZKNaming zkNaming = null;
		final String zooHost = args[0];
		final String zooPort = args[1];
		final String host = args[2];
		final String port = args[3];
		final String path = args[4];

		try {
			zkNaming = new ZKNaming(zooHost, zooPort);
			// publish
			zkNaming.rebind(path, host, port);
			
			System.out.println(SiloServerApp.class.getSimpleName());		
			
			// check arguments
			if (args.length < 1) {
				System.err.println("Argument(s) missing!");
				System.err.printf("Usage: java %s port%n", SiloServerApp.class.getName());
				return;
			}
			
			final BindableService impl = new SiloServiceImpl();
			
			// Create a new server to listen on port
			Server server = ServerBuilder.forPort(Integer.parseInt(port)).addService(impl).build();
			
			// Start the server
			server.start();
			
			// Server threads are running in the background.
			System.out.println("Server started");
			
			// Do not exit the main thread. Wait until server is terminated.
			server.awaitTermination();

		} catch (ZKNamingException e) {
			System.err.println("There was an error with ZKNamingException: " + e);
		} finally  {
			if (zkNaming != null) {
				try {
					// remove
					zkNaming.unbind(path,host,port);
				} catch (ZKNamingException e) {
					System.err.println("There was an error with ZKNamingException: " + e);
				}
			}
		}
	}
		
}
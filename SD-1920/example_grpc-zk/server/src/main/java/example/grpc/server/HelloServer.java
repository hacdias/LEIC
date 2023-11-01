package example.grpc.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;

public class HelloServer {

	public static void main(String[] args) throws Exception {
		System.out.println(HelloServer.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		if (args.length < 5) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s path zooHost zooPort host port%n", Server.class.getName());
			return;
		}

		final String zooHost = args[0];
		final String zooPort = args[1];
		final String path = args[2];
		final String host = args[3];
		final String port = args[4];
		final BindableService impl = new HelloWorldServiceImpl();
		ZKNaming zkNaming = null;

		try {

			zkNaming = new ZKNaming(zooHost, zooPort);
			zkNaming.rebind(path, host, port);

			// Create a new server to listen on port
			Server server = ServerBuilder.forPort(Integer.parseInt(port)).addService(impl).build();

			// Start the server
			server.start();

			// Server threads are running in the background.
			System.out.println("Server started");

			// Do not exit the main thread. Wait until server is terminated.
			server.awaitTermination();
		} finally {
			if (zkNaming != null) {
				zkNaming.unbind(path, host, String.valueOf(port));
			}
		}
	}

}

package pt.tecnico.sauron.silo;


import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.io.IOException;

public class SiloServerApp{

	public static void main(String[] args) throws IOException, InterruptedException, ZKNamingException {
		System.out.println(SiloServerApp.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", SiloServerApp.class.getName());
			return;
		}

		String prefix_path = "/grpc/sauron/silo/";

		/* zooHost zooPort i host port */


		final String zooHost = args[0];
		final String zooPort = args[1];

		final String path = prefix_path + args[2];
		System.out.println("path = " + path);

		final String serverHost = args[3];
		final String serverPort = args[4];


		final BindableService impl = new SiloServerImpl();


		ZKNaming zkNaming = null;
		try {


			zkNaming = new ZKNaming(zooHost, zooPort);
			// publish
			zkNaming.rebind(path, serverHost, serverPort);

			/* Start grpc server */
			// Create a new server to listen on port
			Server server = ServerBuilder.forPort(Integer.parseInt(serverPort)).addService(impl).build();

			// Start the server
			server.start();

			// Server threads are running in the background.
			System.out.println("Server started");

			// Do not exit the main thread. Wait until server is terminated.
			server.awaitTermination();

		} catch (ZKNamingException e) {
			e.printStackTrace();
		} finally {
			if (zkNaming != null) {
				// remove
				zkNaming.unbind(path, serverHost, serverPort);
			}
		}
	}

}


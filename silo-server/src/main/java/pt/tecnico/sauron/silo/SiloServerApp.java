package pt.tecnico.sauron.silo;


import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class SiloServerApp{

	private static ZKNaming zkNaming;
	private static String prefix_path = "/grpc/sauron/silo";

	private static int UPDATE_TIMER = 10000; // -> 30 seconds is 30 000 miliseconds

	private static int replicaNro = 1;

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

		//String prefix_path = "/grpc/sauron/silo/";

		/* zooHost zooPort i host port */


		final String zooHost = args[0];
		final String zooPort = args[1];

		final String path = prefix_path + "/" + args[2];
		System.out.println("path = " + path);

		final String serverHost = args[3];
		final String serverPort = args[4];


		//final BindableService impl = new SiloServerImpl();


		zkNaming = null;
		try {


			zkNaming = new ZKNaming(zooHost, zooPort);
			// publish
			zkNaming.rebind(path, serverHost, serverPort);

			final SiloServerImpl impl = new SiloServerImpl(prefix_path, zooHost, zooPort, replicaNro, args[2]);

			/* Start grpc server */
			// Create a new server to listen on port
			Server server = ServerBuilder.forPort(Integer.parseInt(serverPort)).addService(impl).build();


			// Start the server
			server.start();

			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					impl.update();
				}
			}, UPDATE_TIMER, UPDATE_TIMER);


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


package pt.tecnico.sauron.silo.client;


import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass;

public class SiloClientApp {

	public static void main(String[] args) {
		System.out.println(SiloClientApp.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		SiloFrontend frontend = new SiloFrontend(args[0], args[1]);

		ctrl_ping(frontend);

	}

//	testar no SiloIT
	public static void ctrl_ping(SiloFrontend library) {

//		maybe later send as arg the string for setPing
		try {
			SiloOuterClass.PingRequest request = SiloOuterClass.PingRequest.newBuilder().setPing("Fine").build();
			SiloOuterClass.PingResponse response = library.ctrlPing(request);
			System.out.println(response);

		} catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " +
					e.getStatus().getDescription());
		}
	}

}

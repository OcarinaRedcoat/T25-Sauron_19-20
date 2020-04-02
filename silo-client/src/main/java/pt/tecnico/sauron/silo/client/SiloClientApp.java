package pt.tecnico.sauron.silo.client;


import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.silo.grpc.SiloGrpc;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass;

public class SiloClientApp {

	private  SiloGrpc.SiloBlockingStub stub;

	public static void main(String[] args) {
		System.out.println(SiloClientApp.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		ctrl_ping(args[0], args[1]);

	}

//	testar no SiloIT
	public static void ctrl_ping(String host, String port) {

		SiloFrontend frontend = new SiloFrontend(host, port);

		try {
			SiloOuterClass.PingRequest request = SiloOuterClass.PingRequest.newBuilder().setPing("").build();
			SiloOuterClass.PingResponse response = frontend.ctrlPing(request);
			System.out.println(response);
		} catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " +
					e.getStatus().getDescription());
		}
	}

}

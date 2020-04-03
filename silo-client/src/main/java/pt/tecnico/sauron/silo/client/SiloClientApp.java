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

		SiloFrontend frontend = new SiloFrontend();
		frontend.createChannel(args[0], args[1]);
//
		ctrl_ping(frontend);

	}

//	estas funcoes servem apenas para testar a partir do client

	public static void ctrl_ping(SiloFrontend library) {

//		maybe later send as arg the string for setPing
		try {
			SiloOuterClass.PingRequest request = SiloOuterClass.PingRequest.newBuilder().setPing("").build();
			SiloOuterClass.PingResponse response = library.ctrlPing(request);
			System.out.println(response);

		} catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " +
					e.getStatus().getDescription());
		}
	}

	public static void ctrl_clear(SiloFrontend library) {

	}

	public static void ctrl_init(/*para testar aqui: vai ter de receber os args para a cricao do frontend*/) {
		
	}

}

package pt.tecnico.sauron.spotter;


import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.silo.client.SiloFrontend;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;


public class SpotterApp {

	private static SiloFrontend library;

	public static void main(String[] args) {
		System.out.println(SpotterApp.class.getSimpleName());

		if (args.length != 2){
			System.out.println("Missing Arguments");
			return;
		}

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		library = new SiloFrontend();
		ManagedChannel channel = library.createChannel(args[0], args[1]);

		try (Scanner scanner = new Scanner(System.in)){

			while(scanner.hasNextLine()){

				String command = scanner.next();	// spot ou trail
				String info = scanner.nextLine();	// example: person 26r8723 ou car 67890*
				int size = info.length();			// size of info

				if(command.equals("spot")) {		// spot: track or trackMatch, depending on having * // car *


					if (info.substring(1,4).startsWith("car") && info.substring(5, size).contains("*")) {
						try {
							System.out.print(library.trackMatch("car", info.substring(5, size)));
						} catch (StatusRuntimeException e) {
							Status status = e.getStatus();
							System.out.println(status.getDescription());
						}
					} else if (info.substring(1,4).startsWith("car") && !(info.substring(5, size).contains("*"))) {
						try {
							System.out.println(library.track("car", info.substring(5, size)));
						} catch (StatusRuntimeException e) {
							Status status = e.getStatus();
							System.out.println(status.getDescription());
						}
					} else if(info.substring(1,7).startsWith("person") && info.substring(8, size).contains("*")) {
						try {
							System.out.print(library.trackMatch("person", info.substring(8, size)));
						} catch (StatusRuntimeException e) {
							Status status = e.getStatus();
							System.out.println(status.getDescription());
						}
					} else if(info.substring(1,7).startsWith("person") && !(info.substring(8, size).contains("*"))) {
						try {
							System.out.println(library.track("person", info.substring(8, size)));
						} catch (StatusRuntimeException e) {
							Status status = e.getStatus();
							System.out.println(status.getDescription());
						}
					}
					else {
						System.out.println(("Escreveu mal o spot"));
					}
				}
				else if (command.equals("trail")) {		// trail: only trace
					if(info.substring(1,7).startsWith("person") && !(info.substring(8, size).contains("*"))) {
						try {
							System.out.print(library.trace("person", info.substring(8, size)));
						} catch (StatusRuntimeException e) {
							Status status = e.getStatus();
							System.out.println(status.getDescription());
						}
					}
					else if (info.substring(1,4).startsWith("car") && !(info.substring(5, size).contains("*"))) {
						try {
							System.out.print(library.trace("car", info.substring(5, size)));
						} catch (StatusRuntimeException e) {
							Status status = e.getStatus();
							System.out.println(status.getDescription());
						}
					}
					else {
						System.out.println(("Escreveu o trail mal"));
					}
				}
				else {
					System.out.println(("Exceção, i guess"));
				}

			}

		}
		channel.shutdownNow();
	}

}
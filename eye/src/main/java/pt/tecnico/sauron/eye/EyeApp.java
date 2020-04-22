package pt.tecnico.sauron.eye;

import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.silo.client.SiloFrontend;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class EyeApp {

	private static SiloFrontend library;

	public static void main(String[] args) throws InterruptedException {
		System.out.println(EyeApp.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		String name = args[2]; // camera name
		float latitude = Float.parseFloat(args[3]);
		float longitude = Float.parseFloat(args[4]);

		library = new SiloFrontend();
		ManagedChannel channel = library.createChannel(args[0], args[1]);

		try {
			library.camJoin(name, latitude, longitude);
		} catch (StatusRuntimeException e) {
			Status status = e.getStatus();
			System.out.println(status.getDescription());
			return;
		}


		System.out.print("\nWelcome to EyeApp, type in a report\n\n");
		// TODO: guardar os reports numa lista, apos dois \n enviar essa mesma lista
		// FIXME: front end tem de receber uma lista de report em vez de report
		List<String> type =  new ArrayList<>();
		List<String> id =  new ArrayList<>();
		try (Scanner scanner = new Scanner(System.in)){

			do {

				String token = scanner.nextLine();
				int size = token.length();

				if (token.contains("#")) {
					continue;
				}
				else if (token.contains("zzz")) {
					System.out.println();
					TimeUnit.MILLISECONDS.sleep(Integer.parseInt(token.substring(4, size)));
				}
				else {

					if (token.startsWith("person")) { /* aka person*/
						try {
							type.add("person");
							id.add(token.substring(7, size));
							//library.report("person", token.substring(7, size), name);
						} catch (StatusRuntimeException e) {
							Status status = e.getStatus();
							System.out.println(status.getDescription());
						}
					}
					else if (token.startsWith("car")) { /* aka car */
						try {
							type.add("car");
							id.add(token.substring(4, size));
							//library.report("car", token.substring(4, size), name);
						} catch (StatusRuntimeException e) {
							Status status = e.getStatus();
							System.out.println(status.getDescription());
						}

					}
					else if (token.isEmpty()){
						if (type.size() == 0 || id.size() == 0){
							System.out.println("O ruca hoje não foi ao cabeleireiro");
							continue;
						}
						System.out.println("Será que o ruca vai ao cabeleireiro?");
						library.report(type, id, name);
						type.clear();
						id.clear();
					}
					else {
						System.out.println("Enganou-se a escrever");
					}
				}

			}while (scanner.hasNextLine());

		}
		channel.shutdownNow();
	}
}

package pt.tecnico.sauron.eye;

import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.silo.client.SiloFrontend;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class EyeApp {

	private static SiloFrontend library;

	public static void main(String[] args) throws InterruptedException, ZKNamingException {
		System.out.println(EyeApp.class.getSimpleName());

		if (args.length != 5){
			System.out.println("Missing Arguments");
			return;
		}

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		String name = args[2]; // camera name

		try {
			Float.parseFloat(args[3]);
			Float.parseFloat(args[4]);
		} catch (NumberFormatException nfe){
			System.out.println("Longitude or Latitude not floats.");
			return;
		}

		library = new SiloFrontend();
		ManagedChannel channel = library.createChannel(args[0], args[1], args[3]);

		try {
			library.camJoin(name, args[3], args[4]);
		} catch (StatusRuntimeException e) {
			Status status = e.getStatus();
			System.out.println(status.getDescription());
			channel.shutdownNow();
			return;
		}


		System.out.print("\nWelcome to EyeApp, type in a report\n\n");

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
						type.add("person");
						id.add(token.substring(7, size));

					}
					else if (token.startsWith("car")) { /* aka car */
						type.add("car");
						id.add(token.substring(4, size));

					}

					else if (token.isEmpty() && type.size() != 0 && id.size() != 0){
						try {
						System.out.println("Será que o ruca vai ao cabeleireiro?");
						library.report(type, id, name);
						type.clear();
						id.clear();
						} catch (StatusRuntimeException e) {
							Status status = e.getStatus();
							System.out.println(status.getDescription());
						}
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

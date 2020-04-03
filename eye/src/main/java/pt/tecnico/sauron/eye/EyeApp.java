package pt.tecnico.sauron.eye;

import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.silo.client.SiloFrontend;
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

		String name = args[3]; // camera name
		float latitude = Float.parseFloat(args[4]);
		float longitude = Float.parseFloat(args[5]);

		library = new SiloFrontend();
		ManagedChannel channel = library.createChannel(args[1], args[2]);

		try {
			library.camJoin(name, latitude, longitude);
		} catch (StatusRuntimeException e) {
			Status status = e.getStatus();
			System.out.println(status.getDescription());
			return;
		}


		System.out.print("\nWelcome to EyeApp, type in a report\n\n");

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
							library.report("person", token.substring(7, size), name);
						} catch (StatusRuntimeException e) {
							Status status = e.getStatus();
							System.out.println(status.getDescription());
						}
					}
					else if (token.startsWith("car")) { /* aka car */
						try {
							library.report("car", token.substring(4, size), name);
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

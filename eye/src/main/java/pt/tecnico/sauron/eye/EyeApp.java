package pt.tecnico.sauron.eye;

import pt.tecnico.sauron.silo.client.SiloFrontend;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class EyeApp {

	private static SiloFrontend library;
	private static String name;

	public static void main(String[] args) throws InterruptedException {
		System.out.println(EyeApp.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}


		library = new SiloFrontend(args[1], args[2]);

		name = args[3]; // camera name

		float latitude = Float.parseFloat(args[4]);
		float longitude = Float.parseFloat(args[5]);

		library.camJoin(args[3], latitude, longitude); // args[2] -> name


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
					TimeUnit.SECONDS.sleep(Integer.parseInt(token.substring(4, size)));
//					System.out.print("acabou o sleep\n");
				}
				else {

					if (token.startsWith("person")) { /* aka person*/
						library.report("person", token.substring(7, size), name);
//						System.out.println("PERSON!!!");
					}
					else if (token.startsWith("car")) { /* aka car */
						library.report("car", token.substring(4, size), name);
//						System.out.println("CAAAAAR!!!!");
					}
				}

			}while (scanner.hasNextLine());

		}
	}
}

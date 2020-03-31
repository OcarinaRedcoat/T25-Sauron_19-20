package pt.tecnico.sauron.eye;


import pt.tecnico.sauron.silo.client.SiloFrontend;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class EyeApp {

//	private static SiloFrontend library;
//	private static String name;

	public static void main(String[] args) throws InterruptedException {
		System.out.println(EyeApp.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

//		String[] hpToLib = new String[2]; //host and port to Library (SiloFrontend library)
//		hpToLib[0] = args[1];
//		hpToLib[1] = args[2];
//		System.out.println("deu merda" + hpToLib[0]);


//		int argsSize = args.toString().length();

//		se calhar usar o substring para o FE
//		library = new SiloFrontend(args.toString().substring(3, argsSize));
		System.out.println("deu merda\n");

		SiloFrontend library = new SiloFrontend(args);


		String name = args[2]; // camera name

		float latitude = Float.parseFloat(args[3]);
		float longitude = Float.parseFloat(args[4]);

		library.camJoin(args[2], latitude, longitude); // args[2] -> name



		Scanner scanner = new Scanner("Welcome to EyeApp, type in a report");

		while (scanner.hasNext()){

			String token = scanner.next();
			String currentLine = scanner.nextLine();

			System.out.println("line:" + currentLine);

			int size =  currentLine.length();

			if (token.equals("#")) {
				System.out.println("\n"); /* eu sou um genio */
			}
			else if (currentLine.substring(0, 3).equals("zzz")) {
				TimeUnit.SECONDS.sleep(Integer.parseInt(currentLine.substring(4, size)));
			}
			else {

				if (token.equals("p")) { /* aka person*/
					library.report("person", currentLine.substring(6, size), name);
				}
				else { /* aka car */
					library.report("car", currentLine.substring(3, size), name);
				}
			}
		}
	}
}

package pt.tecnico.sauron.spotter;


//import pt.tecnico.sauron.silo.client.SiloFrontend;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;


public class SpotterApp {

	public static void main(String[] args) {
		System.out.println(SpotterApp.class.getSimpleName());


		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		//SiloFrontend library = new SiloFrontend(args);

//		Scanner scanner = new Scanner("Welcome to SpotterApp, type in you question");
//		System.out.println("Antes do while");




		try (Scanner scanner = new Scanner(System.in)){

			//while(true) {
			while(scanner.hasNextLine()){
				System.out.println("detro do while");

				String command = scanner.next();	// spot ou trail
				String info = scanner.nextLine();	// example: person 26r8723 ou car 67890*
				int size = info.length();			// size of info

				if(command.equals("spot")) {		// spot: track or trackMatch, depending on having *
					System.out.println("é spot");
					if(info.startsWith("person") & info.substring(7, size).contains("*")) {
						System.out.println("spot person COM asterisco");
						//library.trackMatch("person", info.substring(7, size));
					}
					else if (info.startsWith("car") & info.substring(4, size).contains("*")) {
						System.out.println("spot car COM asterisco");
						//library.trackMatch("car", info.substring(4, size));
					}
					else if(info.startsWith("person") & !info.substring(7, size).contains("*")) {
						System.out.println("spot person SEM asterisco");
						//library.track("person", info.substring(7, size));
					}
					else if (info.startsWith("car") & !info.substring(4, size).contains("*")) {
						System.out.println("spot car SEM asterisco");
						//library.track("car", info.substring(4, size));
					}
					else {
						System.out.println(("Deves ter escrito mal o spot, pá"));	//FIXME exceção?
					}
				}
				else if (command.equals("trail")) {		// trail: only trace
					System.out.println("é trail");
					if(info.startsWith("person")) {
						System.out.println("trail person");
						//library.trace("person", info.substring(7, size));
					}
					else if (info.startsWith("car")) {
						System.out.println("trail car");
						//library.trace("car", info.substring(4, size));
					}
					else {
						System.out.println(("Deves ter escrito mal o trail, pá"));	//FIXME exceção?
					}
				}
				else {
					System.out.println(("Exceção, i guess"));	//FIXME exceção?
				}

			}

		}







	}







}

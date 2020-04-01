package pt.tecnico.sauron.spotter;


import pt.tecnico.sauron.silo.client.SiloFrontend;

public class SpotterApp {

	private static SiloFrontend library;

	public static void main(String[] args) {
		System.out.println(SpotterApp.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		String[] hpToLib = new String[2]; //hot and port to Library (SiloFrontend library)
		hpToLib[0] = args[1]; hpToLib[1] = args[2];
		//library = new SiloFrontend(hpToLib);


		while(true){
			// fica as espera de uma entrada
			// deve fazer sempre a verificacao de erros
			// spot -> pode ser track ou trackMatch dependendo da existencia de *
			// trail apenas pode ser trace com ou sem *
		}

	}

}

package pt.tecnico.sauron.eye;


import pt.tecnico.sauron.silo.client.SiloFrontend;

public class EyeApp {

	private static SiloFrontend library;

	public static void main(String[] args) {
		System.out.println(EyeApp.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		//

		String[] hpToLib = new String[2]; //hot and port to Library (SiloFrontend library)
		hpToLib[0] = args[1]; hpToLib[1] = args[2];
		library = new SiloFrontend(hpToLib);

		float latitude = Float.parseFloat(args[3]);
		float longitude = Float.parseFloat(args[4]);

		library.createCamera(args[2], latitude, longitude); // args[2] -> name

	}
}

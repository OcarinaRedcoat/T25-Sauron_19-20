package pt.tecnico.sauron.eye;


import pt.tecnico.sauron.silo.client.SiloFrontend;

public class EyeApp {

	private static SiloFrontend library;
	private static String name;

	public static void main(String[] args) {
		System.out.println(EyeApp.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		library = new SiloFrontend(args);

		name = args[2]; // camera name

		float latitude = Float.parseFloat(args[3]);
		float longitude = Float.parseFloat(args[4]);

		library.camJoin(args[2], latitude, longitude); // args[2] -> name

		while (true){
			//loop de à espera de entradas
			//
			// quando recebe argumentos faz verificacao e manda excecoes s enecessario
			// fazer verificacao de comentario, e sleep, exp # e zzz
			// caso contratio chama a library.report(type , id , this.name ) -> nome da camara
		}
	}
}

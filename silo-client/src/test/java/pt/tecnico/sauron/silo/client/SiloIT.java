package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass;

import java.util.List;

import static io.grpc.Status.ALREADY_EXISTS;
import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SiloIT extends BaseIT {

	private static  SiloFrontend frontEnd;
	// static members
	// TODO	
	

	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp() {
	}

	@AfterAll
	public static void oneTimeTearDown() {
	}

	// initialization and clean-up for each test

	@BeforeEach
	public void setUp() {

		frontEnd = new SiloFrontend();
		frontEnd.createChannel("localhost", "8080");
	}

	@AfterEach
	public void tearDown() {

		frontEnd.ctrlClear();
	}


	@Test
	public void pingOKTest() {
		//SiloOuterClass.PingRequest request = SiloOuterClass.PingRequest.newBuilder().setPing("ping").build();
		assertEquals("ping pong!", frontEnd.ctrlPing("ping"));
	}
		
	// test T1

	@Test
	public void camJoinOK() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");
	}

	@Test
	public void duplicateCam() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");
		assertEquals(ALREADY_EXISTS.getDescription(),
				assertThrows(StatusRuntimeException.class, () -> frontEnd.camJoin("Tagus", "15.0", "30.0")).getStatus().getDescription());
	}

	@Test
	public void camInfo() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");
		frontEnd.camInfo("Tagus");
	}

	/*@Test
	public void nullCamInfo() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");
		assertEquals(ALREADY_EXISTS.getDescription(),
				assertThrows(StatusRuntimeException.class, () -> frontEnd.camJoin("Tagus", "15.0", "30.0")).getStatus().getDescription());
	}*/

	@Test
	public void reportOK() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");
		List<String> types = List.of("car", "person", "person");
		List<String> ids = List.of("HJ23FG", "11", "12");
		frontEnd.report(types, ids, "Tagus");
	}

	@Test
	public void trackOK() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");

		List<String> types = List.of("car");
		List<String> ids = List.of("HJ23FG");

		frontEnd.report(types, ids, "Tagus");
		String obs = frontEnd.track(types.get(0), ids.get(0));

		int obs_size = types.get(0).length() + ids.get(0).length() + 1;

		// provavelmente precisa de for para percorrer lista
		assertEquals(types.get(0) + ',' + ids.get(0), obs.substring(0,obs_size));
	}

	/*@Test
	public void trackMatchOK() {

		frontEnd.camJoin("Tagus", "10.0", "20.0");

		List<String> types = List.of("car");
		List<String> ids = List.of("HJ23FG");

		frontEnd.report(types, ids, "Tagus");



	}*/









}

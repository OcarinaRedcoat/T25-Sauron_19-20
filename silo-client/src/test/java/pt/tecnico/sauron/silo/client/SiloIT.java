package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass;

import java.util.ArrayList;
import java.util.List;

import static io.grpc.Status.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SiloIT extends BaseIT {

	private static  SiloFrontend frontEnd;
	// static members
	// TODO	
	

	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp() {
		frontEnd = new SiloFrontend();
		frontEnd.createChannel("localhost", "8080");
	}

	@AfterAll
	public static void oneTimeTearDown() {
	}

	// initialization and clean-up for each test

	@BeforeEach
	public void setUp() {
	}

	@AfterEach
	public void tearDown() {
		frontEnd.ctrlClear();
	}

		
	// test T1

	@Test
	public void camJoinOK() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");
	}

	@Test
	public void duplicateCamJoin() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");
		assertEquals(ALREADY_EXISTS.getCode(),
				assertThrows(StatusRuntimeException.class, () -> frontEnd.camJoin("Tagus", "15.0", "30.0")).getStatus().getCode());
	}

	@Test
	public void camInfo() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");
		frontEnd.camInfo("Tagus");
	}


	@Test
	public void reportOK() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");
		List<String> types = List.of("car", "person", "person");
		List<String> ids = List.of("HJ23FG", "11", "12");
		frontEnd.report(types, ids, "Tagus");
	}

	@Test
	public void reportINVALID_ARGUMENT() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");

		List<String> types = List.of("car", "person", "person");
		List<String> ids = List.of("HJ23FG123123", "11", "12");

		assertEquals(INVALID_ARGUMENT.getCode(),
				assertThrows(StatusRuntimeException.class, () -> frontEnd.report(types, ids, "Tagus")).getStatus().getCode());
	}

	@Test
	public void trackOK() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");

		List<String> types = List.of("car", "person");
		List<String> ids = List.of("HJ23FG", "12345");

		frontEnd.report(types, ids, "Tagus");
		String obs1 = frontEnd.track(types.get(0), ids.get(0));
		String obs2 = frontEnd.track(types.get(1), ids.get(1));

		int obs1_size = types.get(0).length() + ids.get(0).length() + 1;
		int obs2_size = types.get(1).length() + ids.get(1).length() + 1;

		// provavelmente precisa de for para percorrer lista
		assertEquals(types.get(0) + ',' + ids.get(0), obs1.substring(0,obs1_size));
		assertEquals(types.get(1) + ',' + ids.get(1), obs2.substring(0,obs2_size));

	}

	/* ee redudante ter invalid argument  no track pois o report ja trata dessa exception */

	@Test
	public void trackNOT_FOUND() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");

		List<String> types = List.of("car", "person");
		List<String> ids = List.of("HJ23FG", "12345");

		frontEnd.report(types, ids, "Tagus");

		assertEquals(NOT_FOUND.getCode(), assertThrows(StatusRuntimeException.class, () -> frontEnd.track("car", "7479RV")).getStatus().getCode());
	}

	/*@Test
	public void trackMatchOK() {

		frontEnd.camJoin("Tagus", "10.0", "20.0");

		List<String> types = List.of("car", "car");
		List<String> ids = List.of("HJ23FG", "7479RV");

		frontEnd.report(types, ids, "Tagus");
		String observations = frontEnd.trackMatch(types.get(0), "*");

		int obs_size = types.get(0).length() + ids.get(0).length() + 1;
		assertEquals(types.get(1) + ',' + ids.get(1), observations.substring(0,obs_size));

		System.out.println(ind);
		System.out.println(observations.substring(0, ind));


	}*/










}

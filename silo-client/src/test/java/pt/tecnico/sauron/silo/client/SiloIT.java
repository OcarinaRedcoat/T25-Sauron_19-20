package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

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
	public static void oneTimeSetUp() throws ZKNamingException {
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


	@Test
	public void camJoinOK() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");
	}

	@Test
	public void camJoinALREADY_EXISTS() {
		frontEnd.camJoin("Tagus", "10.0", "20.0");
		assertEquals(ALREADY_EXISTS.getCode(),
				assertThrows(StatusRuntimeException.class, () -> frontEnd.camJoin("Tagus", "15.0", "30.0")).getStatus().getCode());
	}

	@Test
	public void camJoinINVALID_ARGUMENT() {
		assertEquals(INVALID_ARGUMENT.getCode(),
				assertThrows(StatusRuntimeException.class, () -> frontEnd.camJoin("Tagus", "String", "Vector de Strings que no java pode ser imensas coisa o que ee soo triste")).getStatus().getCode());
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


	@Test
	public void trackINVALID_ARGUMENT() {
		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, () -> frontEnd.track("car", "AAAARV")).getStatus().getCode());
	}

	@Test
	public void trackNOT_FOUND() {
		assertEquals(NOT_FOUND.getCode(), assertThrows(StatusRuntimeException.class, () -> frontEnd.track("car", "7479RV")).getStatus().getCode());
	}

	@Test
	public void TrackMatchOK() {

		frontEnd.camJoin("Tagus", "10.0", "20.0");

		List<String> types = List.of("car", "car");
		List<String> ids = List.of("HJ23FG", "7479RV");

		frontEnd.report(types, ids, "Tagus");
		String obs = frontEnd.trackMatch(types.get(0), "H*");

		int obs_size = types.get(0).length() + ids.get(0).length() + 1;

		assertEquals(types.get(0) + ',' + ids.get(0), obs.substring(0, obs_size));
	}


	@Test
	public void multipleTrackMatchOK() {

		frontEnd.camJoin("Tagus", "10.0", "20.0");

		List<String> types = List.of("car", "car");
		List<String> ids = List.of("HJ23FG", "7479RV");

		frontEnd.report(types, ids, "Tagus");
		String observations = frontEnd.trackMatch(types.get(0), "*");

		int obs_size = types.get(0).length() + ids.get(0).length() + 1;
		List<String> result = new ArrayList<>();


		for (String val: observations.split("\\n")) {
			result.add(val);
		}

		assertEquals(types.get(1) + ',' + ids.get(1), result.get(0).substring(0, obs_size));
		assertEquals(types.get(0) + ',' + ids.get(0), result.get(1).substring(0, obs_size));
	}


	@Test
	public void trackMatchNOT_FOUND() {
		assertEquals(NOT_FOUND.getCode(), assertThrows(StatusRuntimeException.class, () -> frontEnd.trackMatch("car", "P*")).getStatus().getCode());

	}


	@Test
	public void traceOK() {

		frontEnd.camJoin("Tagus", "10.0", "20.0");
		frontEnd.camJoin("Alameda", "15.0", "20.0");

		List<String> typesT = List.of("car");
		List<String> idsT = List.of("AA11BB");

		List<String> typesA = List.of("car");
		List<String> idsA = List.of("AA11BB");

		frontEnd.report(typesT, idsT, "Tagus");
		frontEnd.report(typesA, idsA, "Alameda");

		String observations = frontEnd.trace(typesT.get(0), idsT.get(0));

		int obs_size = typesT.get(0).length() + idsT.get(0).length() + 1;

		//com virgula
		int ISOsize = 29;
		List<String> result = new ArrayList<>();


		for (String val: observations.split("\\n")) {
			result.add(val);
		}

		// test Tagus obs
		assertEquals(typesT.get(0) + ',' + idsT.get(0), result.get(1).substring(0, obs_size));
		assertEquals("Tagus", result.get(1).substring(obs_size + ISOsize, obs_size + ISOsize + 5)); // 5 corresponde ao size da string Tagus

		// test Alameda obs
		assertEquals(typesA.get(0) + ',' + idsA.get(0), result.get(0).substring(0, obs_size));
		assertEquals("Alameda", result.get(0).substring(obs_size + ISOsize, obs_size + ISOsize + 7)); // 7 corresponde ao size da string Alameda
	}

	@Test
	public void traceINVALID_ARGUMENT() {
		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, () -> frontEnd.trace("car", "1111")).getStatus().getCode());
	}

	@Test
	public void traceNOT_FOUND() {
		assertEquals(NOT_FOUND.getCode(), assertThrows(StatusRuntimeException.class, () -> frontEnd.trace("car", "50HG55")).getStatus().getCode());
	}






}

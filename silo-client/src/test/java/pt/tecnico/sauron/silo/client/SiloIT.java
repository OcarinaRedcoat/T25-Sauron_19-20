package pt.tecnico.sauron.silo.client;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SiloIT extends BaseIT {

	private static  SiloFrontend frontEnd;
	// static members
	// TODO	
	

	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp() {
//		ctrl_init
	}

	@AfterAll
	public static void oneTimeTearDown() {
//		ctrl_reset
	}

	// initialization and clean-up for each test

	@BeforeEach
	public void setUp() {
		frontEnd = new SiloFrontend("localhost", "8080");
	}

	@AfterEach
	public void tearDown() {
		frontEnd = null;
	}
		
	// test T1
	
	@Test
	// Teste ao T1, cam_info cam_join eye
	public void testSetNullCamJoin() {
//		assertThrows()
	}

	@Test
	public void testGetCamJoin() {
//		assertEquals(, FrontEnd.camJoin(););
	}

	@Test
	public void testSetNullCamInfo() {
//		assertThrows();
	}

	@Test
	public void testGetCamInfo() {

//		frontEnd.camJoin("Tagus", 10.5f, 10.5f);
//		float[] expected = {10.5f, 10.5f};
//
//		assertEquals(expected, frontEnd.camInfo("Tagus"));
	}

	@Test
	public void failingTestT1() {

	}


















	// Teste ao T2, report, spotter
	@Test
	public void testT2() {

	}

	@Test
	public void failingTestT2() {

	}

	// Teste ao T3, track, trackMatch, trace
	@Test
	public void testT3() {

	}

	@Test
	public void failingTestT3() {

	}

}

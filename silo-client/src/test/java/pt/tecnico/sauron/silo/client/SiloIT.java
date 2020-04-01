package pt.tecnico.sauron.silo.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pt.tecnico.sauron.silo.client.domain.SiloFrontend;
import pt.tecnico.sauron.silo.client.exceptions.SauronClientException;

public class SiloIT extends BaseIT {
	public static SiloFrontend api;

	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp() {
		String host = testProps.getProperty("server.host");
		Integer port = Integer.parseInt(testProps.getProperty("server.port"));
		api = new SiloFrontend(host, port);
	}

	@AfterAll
	public static void oneTimeTearDown() {
		// EMPTY
	}

	// initialization and clean-up for each test

	@BeforeEach
	public void setUp() {

	}

	@AfterEach
	public void tearDown() throws SauronClientException {
		api.clear();
	}

	@Test
	public void testCamJoin() {
		// TODO
		assertEquals(true, false, "not implemented");
	}

	@Test
	public void testCamInfo() {
		// TODO
		assertEquals(true, false, "not implemented");
	}

	@Test
	public void testReport() {
		// TODO
		assertEquals(true, false, "not implemented");
	}

	@Test
	public void testTrack() {
		// TODO
		assertEquals(true, false, "not implemented");
	}

	@Test
	public void testTrackMatch() {
		// TODO
		assertEquals(true, false, "not implemented");
	}

	@Test
	public void testTrace() {
		// TODO
		assertEquals(true, false, "not implemented");
	}
}

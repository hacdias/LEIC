package pt.tecnico.sauron.silo.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pt.tecnico.sauron.silo.client.domain.Coordinates;
import pt.tecnico.sauron.silo.client.domain.SiloFrontend;
import pt.tecnico.sauron.silo.client.exceptions.DuplicateCameraException;
import pt.tecnico.sauron.silo.client.exceptions.InvalidCameraCoordinatesException;
import pt.tecnico.sauron.silo.client.exceptions.InvalidCameraException;
import pt.tecnico.sauron.silo.client.exceptions.InvalidCameraNameException;
import pt.tecnico.sauron.silo.client.exceptions.SauronClientException;

public class SiloIT extends BaseIT {
	public static SiloFrontend api;

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

	@BeforeEach
	public void setUp() {

	}

	@AfterEach
	public void tearDown() throws SauronClientException {
		api.clear();
	}

	@Test
	public void testCamJoin() throws SauronClientException {
		api.camJoin("MyCamera", -30.95, 50.94);
	}

	@Test
	public void testCamJoinWrongName() {
		Assertions.assertThrows(InvalidCameraNameException.class, () -> api.camJoin("An Invalid and Extra Long Name", 30.0, 90.0));
	}

	@Test
	public void testCamJoinDuplicateCameraByName() throws SauronClientException {
		String name = "MyCamera";
		api.camJoin(name, -30.12, 90.0);
		Assertions.assertThrows(DuplicateCameraException.class, () -> api.camJoin(name, -35.0, -50.0));
	}

	@Test
	public void testCamJoinDuplicateCameraByCoordinates() throws SauronClientException {
		Double latitude = -30.95;
		Double longitude = 50.94;

		api.camJoin("MyCamera", latitude, longitude);
		Assertions.assertThrows(DuplicateCameraException.class, () -> api.camJoin("AnotherCamera", latitude, longitude));
	}

	@Test
	public void testCamJoinWrongLatitude() {
		Assertions.assertThrows(InvalidCameraCoordinatesException.class, () -> api.camJoin("MyCamera", -95.0, 90.0));
	}

	@Test
	public void testCamJoinWrongLongitude() {
		Assertions.assertThrows(InvalidCameraCoordinatesException.class, () -> api.camJoin("MyCamera", 30.0, 190.0));
	}

	@Test
	public void testCamInfo() throws SauronClientException {
		String name = "MyCamera";
		Double latitude = -30.95;
		Double longitude = 50.94;

		api.camJoin(name, latitude, longitude);
		Coordinates coordinates = api.camInfo(name);

		assertEquals(coordinates.getLatitude(), latitude, "correct latitude");
		assertEquals(coordinates.getLongitude(), longitude, "correct longitude");
	}

	@Test
	public void testCamInfoNonExisting() {
		Assertions.assertThrows(InvalidCameraException.class, () -> api.camInfo("AnotherCamera"));
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

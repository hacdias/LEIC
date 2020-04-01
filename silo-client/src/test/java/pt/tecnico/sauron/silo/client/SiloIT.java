package pt.tecnico.sauron.silo.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pt.tecnico.sauron.silo.client.domain.Camera;
import pt.tecnico.sauron.silo.client.domain.Coordinates;
import pt.tecnico.sauron.silo.client.domain.Observation;
import pt.tecnico.sauron.silo.client.domain.ObservationType;
import pt.tecnico.sauron.silo.client.domain.SiloFrontend;
import pt.tecnico.sauron.silo.client.exceptions.DuplicateCameraException;
import pt.tecnico.sauron.silo.client.exceptions.InvalidCameraCoordinatesException;
import pt.tecnico.sauron.silo.client.exceptions.InvalidCameraException;
import pt.tecnico.sauron.silo.client.exceptions.InvalidCameraNameException;
import pt.tecnico.sauron.silo.client.exceptions.InvalidIdentifierException;
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
	public void testCamJoinDuplicateCamera() throws SauronClientException {
		String name = "MyCamera";
		api.camJoin(name, -30.12, 90.0);
		Assertions.assertThrows(DuplicateCameraException.class, () -> api.camJoin(name, -35.0, -50.0));
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
	public void testReport() throws SauronClientException {
		String camName = "Camera1";
		Double latitude = 45.0;
		Double longitude = 45.0;
		Coordinates coordinates = new Coordinates(latitude, longitude);
		Camera camera = new Camera(camName, coordinates);
		api.camJoin(camName, latitude, longitude);

		List<Observation> observations = new ArrayList<Observation>();
		ObservationType type1 = ObservationType.PERSON;
		String identifier1 = "12345";
		Observation observation1 = new Observation(camera, type1, identifier1, LocalDateTime.now());
		observations.add(observation1);
		ObservationType type2 = ObservationType.CAR;
		String identifier2 = "AA22AA";
		Observation observation2 = new Observation(camera, type2, identifier2, LocalDateTime.now());
		observations.add(observation2);

		api.report(camName, observations);
	}

	public void testReportNoRegisteredCamera() throws SauronClientException {
		String camName = "Camera1";
		Double latitude = 45.0;
		Double longitude = 45.0;
		Coordinates coordinates = new Coordinates(latitude, longitude);
		Camera camera = new Camera(camName, coordinates);;

		List<Observation> observations = new ArrayList<Observation>();
		ObservationType type1 = ObservationType.PERSON;
		String identifier1 = "12345";
		Observation observation1 = new Observation(camera, type1, identifier1, LocalDateTime.now());
		observations.add(observation1);
		ObservationType type2 = ObservationType.CAR;
		String identifier2 = "AA22AA";
		Observation observation2 = new Observation(camera, type2, identifier2, LocalDateTime.now());
		observations.add(observation2);

		Assertions.assertThrows(InvalidCameraException.class, () -> api.report(camName, observations));
	}

	@Test
	public void testReportInvalidPersonIdentifier() throws SauronClientException {
		String camName = "Camera1";
		Double latitude = 45.0;
		Double longitude = 45.0;
		Coordinates coordinates = new Coordinates(latitude, longitude);
		Camera camera = new Camera(camName, coordinates);
		api.camJoin(camName, latitude, longitude);

		List<String> identifiers = List.of("AAAA", "1A1");

		ObservationType type = ObservationType.PERSON;
		
		for (String identifier : identifiers) {
			List<Observation> observations = new ArrayList<Observation>();
			Observation observation = new Observation(camera, type, identifier, LocalDateTime.now());
			observations.add(observation);
			Assertions.assertThrows(InvalidIdentifierException.class, () -> api.report(camName, observations), "Failed on: " + identifier);
		}
	}

	@Test
	public void testReportInvalidCarIdentifier() throws SauronClientException {
		String camName = "Camera1";
		Double latitude = 45.0;
		Double longitude = 45.0;
		Coordinates coordinates = new Coordinates(latitude, longitude);
		Camera camera = new Camera(camName, coordinates);
		api.camJoin(camName, latitude, longitude);

		List<String> identifiers = List.of("AAZZAA", "112211", "11A211", "1ABBCC");

		ObservationType type = ObservationType.CAR;
		
		for (String identifier : identifiers) {
			List<Observation> observations = new ArrayList<Observation>();
			Observation observation = new Observation(camera, type, identifier, LocalDateTime.now());
			observations.add(observation);
			Assertions.assertThrows(InvalidIdentifierException.class, () -> api.report(camName, observations), "Failed on: " + identifier);
		}

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

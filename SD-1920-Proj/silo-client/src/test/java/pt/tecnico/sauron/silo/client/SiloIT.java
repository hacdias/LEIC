package pt.tecnico.sauron.silo.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pt.tecnico.sauron.silo.client.domain.Camera;
import pt.tecnico.sauron.silo.client.domain.Observation;
import pt.tecnico.sauron.silo.client.domain.ObservationType;
import pt.tecnico.sauron.silo.client.domain.SiloFrontend;
import pt.tecnico.sauron.silo.client.exceptions.InvalidCameraCoordinatesException;
import pt.tecnico.sauron.silo.client.exceptions.InvalidCameraException;
import pt.tecnico.sauron.silo.client.exceptions.InvalidCameraNameException;
import pt.tecnico.sauron.silo.client.exceptions.InvalidIdentifierException;
import pt.tecnico.sauron.silo.client.exceptions.NoObservationFoundException;
import pt.tecnico.sauron.silo.client.exceptions.SauronClientException;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

public class SiloIT extends BaseIT {
	public static SiloFrontend api;
	public static Integer rtt;

	@BeforeAll
	public static void oneTimeSetUp() throws ZKNamingException, SauronClientException {
		String host = testProps.getProperty("server.host");
		Integer port = Integer.parseInt(testProps.getProperty("server.port"));
		rtt = Integer.parseInt(testProps.getProperty("server.rtt"));
		api = new SiloFrontend(host, port, -1, 0);
	}

	@AfterAll
	public static void oneTimeTearDown() {
		// EMPTY
	}

	@BeforeEach
	public void setUp() {

	}

	@AfterEach
	public void tearDown() throws SauronClientException, ZKNamingException {
		api.clear();
	}

	@Test
	public void testCamJoin() throws SauronClientException, ZKNamingException {
		api.camJoin("MyCamera", -30.95, 50.94);
	}

 	@Test
	public void testCamJoinInvalidNameTooLong() {
		Assertions.assertThrows(InvalidCameraNameException.class, () -> api.camJoin("An Invalid and Extra Long Name", 30.0, 90.0));
	}

	@Test
	public void testCamJoinInvalidNameCharacters() {
		Assertions.assertThrows(InvalidCameraNameException.class, () -> api.camJoin("Not@Name", 30.0, 90.0));
	}

 	@Test
	public void testCamJoinTwice() throws SauronClientException, ZKNamingException {
		api.camJoin("MyCamera", -23.13 , 99.0);
		api.camJoin("MyCamera", -23.13 , 99.0);
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
	public void testCamInfo() throws SauronClientException, ZKNamingException {
		String name = "MyCamera";
		Double latitude = -30.95;
		Double longitude = 50.94;

		api.camJoin(name, latitude, longitude);
		Camera camera = api.camInfo(name);

		assertEquals(camera.getLatitude(), latitude, "correct latitude");
		assertEquals(camera.getLongitude(), longitude, "correct longitude");
	}

	@Test
	public void testCamInfoNonExisting() {
		Assertions.assertThrows(InvalidCameraException.class, () -> api.camInfo("AnotherCamera"));
	}

	@Test
	public void testReport() throws SauronClientException, ZKNamingException {
		String camName = "Camera1";
		Double latitude = 45.0;
		Double longitude = 45.0;
		Camera camera = new Camera(camName, latitude, longitude);
		api.camJoin(camName, latitude, longitude);

		List<Observation> observations = new ArrayList<>();
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

	@Test
	public void testReportInvalidPersonIdentifier() throws SauronClientException, ZKNamingException {
		String camName = "Camera1";
		Double latitude = 45.0;
		Double longitude = 45.0;
		Camera camera = new Camera(camName, latitude, longitude);
		api.camJoin(camName, latitude, longitude);

		List<String> identifiers = List.of("AAAA", "1A1");

		ObservationType type = ObservationType.PERSON;

		for (String identifier : identifiers) {
			List<Observation> observations = new ArrayList<>();
			Observation observation = new Observation(camera, type, identifier, LocalDateTime.now());
			observations.add(observation);
			Assertions.assertThrows(InvalidIdentifierException.class, () -> api.report(camName, observations), "Failed on: " + identifier);
		}
	}

	@Test
	public void testReportInvalidCarIdentifier() throws SauronClientException, ZKNamingException {
		String camName = "Camera1";
		Double latitude = 45.0;
		Double longitude = 45.0;
		Camera camera = new Camera(camName, latitude, longitude);
		api.camJoin(camName, latitude, longitude);

		List<String> identifiers = List.of("AAZZAA", "112211", "11A211", "1ABBCC", "11aa11");

		ObservationType type = ObservationType.CAR;

		for (String identifier : identifiers) {
			List<Observation> observations = new ArrayList<>();
			Observation observation = new Observation(camera, type, identifier, LocalDateTime.now());
			observations.add(observation);
			Assertions.assertThrows(InvalidIdentifierException.class, () -> api.report(camName, observations), "Failed on: " + identifier);
		}

	}

	@Test
	public void testTrack() throws SauronClientException, ZKNamingException {
		String name = "MyCamera";
		Double latitude = -35.45;
		Double longitude = 66.16;

		Camera cam = new Camera(name, latitude, longitude);
		api.camJoin(name, latitude, longitude);

		ObservationType type = ObservationType.PERSON;
		String identifier = "1234";
		LocalDateTime datetime = LocalDateTime.now();

		Observation observationPerson = new Observation(cam, type, identifier, datetime);

		type = ObservationType.CAR;
		identifier = "12ES23";
		datetime = LocalDateTime.now();

		Observation observationCar = new Observation(cam, type, identifier, datetime);
		List<Observation> observations = new ArrayList<>();

		observations.add(observationPerson);
		observations.add(observationCar);

		api.report(name, observations);

		Observation personObservation = api.track(ObservationType.PERSON, "1234");
		Observation carObservation = api.track(ObservationType.CAR, "12ES23");

		assertEquals(observationPerson.getIdentifier(), personObservation.getIdentifier());
		assertEquals(observationPerson.getType(), personObservation.getType());
		assertTrue(personObservation.getDatetime().isAfter(observationPerson.getDatetime().minus(Duration.ofMillis(rtt))));
		assertTrue(personObservation.getDatetime().isBefore(observationPerson.getDatetime().plus(Duration.ofMillis(rtt))));
		assertEquals(name, personObservation.getCamera().getName());
		assertEquals(latitude, personObservation.getCamera().getLatitude());
		assertEquals(longitude, personObservation.getCamera().getLongitude());

		assertEquals(observationCar.getIdentifier(), carObservation.getIdentifier());
		assertEquals(observationCar.getType(), carObservation.getType());
		assertTrue(carObservation.getDatetime().isAfter(observationCar.getDatetime().minus(Duration.ofMillis(rtt))));
		assertTrue(carObservation.getDatetime().isBefore(observationCar.getDatetime().plus(Duration.ofMillis(rtt))));
		assertEquals(name, carObservation.getCamera().getName());
		assertEquals(latitude, carObservation.getCamera().getLatitude());
		assertEquals(longitude, carObservation.getCamera().getLongitude());
	}

	@Test
	public void testTrackNoObservationFound() throws SauronClientException, ZKNamingException {
		String name = "MyCamera";
		Double latitude = -35.45;
		Double longitude = 66.16;

		Camera cam = new Camera(name, latitude, longitude);
		api.camJoin(name, latitude, longitude);

		ObservationType type = ObservationType.PERSON;
		String identifier = "1234";
		LocalDateTime datetime = LocalDateTime.now();

		Observation observationPerson = new Observation(cam, type, identifier, datetime);

		type = ObservationType.CAR;
		identifier = "12ES23";
		datetime = LocalDateTime.now();

		Observation observationCar = new Observation(cam, type, identifier, datetime);
		List<Observation> observations = new ArrayList<>();

		observations.add(observationPerson);
		observations.add(observationCar);

		api.report(name, observations);

		Assertions.assertThrows(NoObservationFoundException.class, () -> api.track(ObservationType.PERSON, "141234567890"));
		Assertions.assertThrows(NoObservationFoundException.class, () -> api.track(ObservationType.CAR, "10ES20"));
	}

	@Test
	public void testTrackMatch() throws SauronClientException, ZKNamingException {
		String name = "MyCamera";
		Double latitude = -35.45;
		Double longitude = 66.16;

		Camera cam = new Camera(name, latitude, longitude);
		api.camJoin(name, latitude, longitude);

		ObservationType type = ObservationType.PERSON;
		String identifier = "1234";
		LocalDateTime datetime = LocalDateTime.now();

		Observation observationPerson = new Observation(cam, type, identifier, datetime);

		type = ObservationType.CAR;
		identifier = "12ES23";
		datetime = LocalDateTime.now();

		Observation observationCar = new Observation(cam, type, identifier, datetime);
		List<Observation> observations = new ArrayList<Observation>();

		observations.add(observationPerson);
		observations.add(observationCar);

		api.report(name, observations);

		List<Observation> personObservations = api.trackMatch(ObservationType.PERSON, "1*3*");
		List<Observation> carObservations = api.trackMatch(ObservationType.CAR, "12*23");

		assertEquals(1, personObservations.size());
		assertEquals(observationPerson.getIdentifier(), personObservations.get(0).getIdentifier());
		assertEquals(observationPerson.getType(), personObservations.get(0).getType());
		assertTrue(personObservations.get(0).getDatetime().isAfter(observationPerson.getDatetime().minus(Duration.ofMillis(rtt))));
		assertTrue(personObservations.get(0).getDatetime().isBefore(observationPerson.getDatetime().plus(Duration.ofMillis(rtt))));
		assertEquals(name, personObservations.get(0).getCamera().getName());
		assertEquals(latitude, personObservations.get(0).getCamera().getLatitude());
		assertEquals(longitude, personObservations.get(0).getCamera().getLongitude());

		assertEquals(1, carObservations.size());
		assertEquals(observationCar.getIdentifier(), carObservations.get(0).getIdentifier());
		assertEquals(observationCar.getType(), carObservations.get(0).getType());
		assertTrue(carObservations.get(0).getDatetime().isAfter(observationCar.getDatetime().minus(Duration.ofMillis(rtt))));
		assertTrue(carObservations.get(0).getDatetime().isBefore(observationCar.getDatetime().plus(Duration.ofMillis(rtt))));
		assertEquals(name, carObservations.get(0).getCamera().getName());
		assertEquals(latitude, carObservations.get(0).getCamera().getLatitude());
		assertEquals(longitude, carObservations.get(0).getCamera().getLongitude());
	}

	@Test
	public void testTrackmatchNoObservationFound() throws SauronClientException, ZKNamingException {
		String name = "MyCamera";
		Double latitude = -35.45;
		Double longitude = 66.16;

		Camera cam = new Camera(name, latitude, longitude);
		api.camJoin(name, latitude, longitude);

		ObservationType type = ObservationType.PERSON;
		String identifier = "1234";
		LocalDateTime datetime = LocalDateTime.now();

		Observation observationPerson = new Observation(cam, type, identifier, datetime);

		type = ObservationType.CAR;
		identifier = "12ES23";
		datetime = LocalDateTime.now();

		Observation observationCar = new Observation(cam, type, identifier, datetime);
		List<Observation> observations = new ArrayList<>();

		observations.add(observationPerson);
		observations.add(observationCar);

		api.report(name, observations);
		Assertions.assertThrows(NoObservationFoundException.class, () -> api.trackMatch(ObservationType.PERSON, "1*3"));
		Assertions.assertThrows(NoObservationFoundException.class, () -> api.trackMatch(ObservationType.CAR, "2*2"));
	}

	@Test
	public void testTrace() throws SauronClientException, ZKNamingException {
		String name = "MyCamera";
		Double latitude = -35.45;
		Double longitude = 66.16;

		Camera cam = new Camera(name, latitude, longitude);
		api.camJoin(name, latitude, longitude);

		ObservationType type = ObservationType.PERSON;
		String identifier = "1234";
		LocalDateTime datetime = LocalDateTime.now();

		Observation observationPerson1 = new Observation(cam, type, identifier, datetime);
		Observation observationPerson2 = new Observation(cam, type, identifier, datetime);

		List<Observation> observations1 = new ArrayList<Observation>();

		observations1.add(observationPerson1);
		api.report(name, observations1);

		List<Observation> observations2 = new ArrayList<Observation>();

		observations2.add(observationPerson2);
		api.report(name, observations2);

		List<Observation> personObservations = api.trace(ObservationType.PERSON, "1234");

		assertEquals(2, personObservations.size());
		assertEquals(observationPerson1.getIdentifier(), personObservations.get(1).getIdentifier());
		assertEquals(observationPerson2.getIdentifier(), personObservations.get(0).getIdentifier());
		assertEquals(observationPerson1.getType(), personObservations.get(1).getType());
		assertEquals(observationPerson2.getType(), personObservations.get(0).getType());
		assertTrue(personObservations.get(0).getDatetime().isAfter(personObservations.get(1).getDatetime()));

		assertTrue(personObservations.get(1).getDatetime().isAfter(observationPerson1.getDatetime().minus(Duration.ofMillis(rtt))));
		assertTrue(personObservations.get(0).getDatetime().isAfter(observationPerson2.getDatetime().minus(Duration.ofMillis(rtt))));
		assertTrue(personObservations.get(1).getDatetime().isBefore(observationPerson1.getDatetime().plus(Duration.ofMillis(rtt))));
		assertTrue(personObservations.get(0).getDatetime().isBefore(observationPerson2.getDatetime().plus(Duration.ofMillis(rtt))));
		assertEquals(name, personObservations.get(0).getCamera().getName());
		assertEquals(name, personObservations.get(1).getCamera().getName());
		assertEquals(latitude, personObservations.get(0).getCamera().getLatitude());
		assertEquals(latitude, personObservations.get(1).getCamera().getLatitude());
		assertEquals(longitude, personObservations.get(0).getCamera().getLongitude());
		assertEquals(longitude, personObservations.get(1).getCamera().getLongitude());
	}

	@Test
	public void testTraceNoObservationFound() throws SauronClientException, ZKNamingException {
		String name = "MyCamera";
		Double latitude = -35.45;
		Double longitude = 66.16;

		Camera cam = new Camera(name, latitude, longitude);
		api.camJoin(name, latitude, longitude);

		ObservationType type = ObservationType.PERSON;
		String identifier = "1234";
		LocalDateTime datetime = LocalDateTime.now();

		Observation observationPerson1 = new Observation(cam, type, identifier, datetime);
		Observation observationPerson2 = new Observation(cam, type, identifier, datetime);

		List<Observation> observations1 = new ArrayList<>();

		observations1.add(observationPerson1);
		api.report(name, observations1);

		List<Observation> observations2 = new ArrayList<>();

		observations2.add(observationPerson2);
		api.report(name, observations2);

		Assertions.assertThrows(NoObservationFoundException.class, () -> api.trace(ObservationType.PERSON, "1324"));
	}
}

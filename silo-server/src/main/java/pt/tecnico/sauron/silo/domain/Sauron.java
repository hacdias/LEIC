package pt.tecnico.sauron.silo.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import pt.tecnico.sauron.silo.exceptions.DuplicateCameraException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraCoordinatesException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraNameException;
import pt.tecnico.sauron.silo.exceptions.InvalidIdentifierException;
import pt.tecnico.sauron.silo.exceptions.NoObservationException;

public class Sauron {
  private static final Logger LOGGER = Logger.getLogger(Sauron.class.getName());
  private List<Observation> observations = new ArrayList<Observation>();
  private List<Camera> cameras = new ArrayList<Camera>();

  public PingInformation ctrlPing() {
    PingInformation information = new PingInformation(cameras, observations);
    LOGGER.info("the system was pinged");
    return information;
  }

  public void ctrlClear() {
    observations = new ArrayList<Observation>();
    cameras = new ArrayList<Camera>();
    LOGGER.info("the system was cleared")
  }

  public void ctrlInit() {
    // TODO
  }

  public void addCamera(String name, Float latitude, Float longitude)
      throws InvalidCameraNameException, InvalidCameraCoordinatesException, DuplicateCameraException {
    try {
      Camera cam = getCamera(name);
      if (!(cam.getName().equals(name) && cam.getCoordinates().getLatitude().equals(latitude) && cam.getCoordinates().getLongitude().equals(longitude))){
        throw new DuplicateCameraException(name);
      }
    } catch (InvalidCameraException e) {
      Coordinates coordinates = new Coordinates(latitude, longitude);
      Camera camera = new Camera(name, coordinates);
      cameras.add(camera);
      LOGGER.info("a new camera was added: " + camera.toString());
    }
  }

  public Camera getCamera(String name) throws InvalidCameraException {
    for (Camera cam : cameras) {
      if (cam.getName().equals(name)) {
        return cam;
      }
    }

    throw new InvalidCameraException(name);
  }

  public Observation track(ObservationType type, String identifier) throws NoObservationException {
    Observation lastObservation = observations.stream()
      .filter(observation -> type == observation.getType() && identifier.equals(observation.getIdentifier()))
      .max(Comparator.comparing(Observation::getDatetime).reversed())
      .orElse(null);

    if (lastObservation == null) {
      throw new NoObservationException(identifier);
    }

    return lastObservation;
  }

  private String buildRegex(ObservationType type, String pattern) {
    String patternRegex = "^".concat(pattern).concat("$");

    switch (type) {
      case PERSON:
        return patternRegex.replace("*", "\\d*");
      case CAR:
        return patternRegex.replace("*", "\\w*");
      default:
        return patternRegex;
    }
  }

  public List<Observation> trackMatch(ObservationType type, String pattern) throws NoObservationException {
    String regex = buildRegex(type, pattern);

    List<List<Observation>> matches = new ArrayList<List<Observation>>(observations.stream()
      .filter(observation -> type == observation.getType() && observation.getIdentifier().matches(regex))
      .collect(Collectors.groupingBy(element -> element.getIdentifier()))
      .values());

    List<Observation> firstMatches = matches.stream()
      .map(element -> observations.stream()
        .max(Comparator.comparing(Observation::getDatetime).reversed())
        .orElse(null))
      .filter(element -> element != null)
      .collect(Collectors.toList());

    return firstMatches;
  }

  public List<Observation> trace(ObservationType type, String identifier) throws NoObservationException {
    List<Observation> observationsMatch = observations.stream()
      .filter(observation -> type == observation.getType() && identifier.equals(observation.getIdentifier()))
      .sorted(Comparator.comparing(Observation::getDatetime).reversed())
      .collect(Collectors.toList());

    if (observationsMatch.size() == 0) {
      throw new NoObservationException(identifier);
    }

    return observationsMatch;
  }

  public void report (String name, ObservationType type, String identifier) throws InvalidCameraException, InvalidIdentifierException {
    Camera camera = getCamera(name);
    Observation observation = new Observation(camera, type, identifier, LocalDateTime.now());
    observations.add(observation);
    LOGGER.info("a new observation was added: " + observation.toString());
  }
}
package pt.tecnico.sauron.silo.domain;

import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pt.tecnico.sauron.silo.exceptions.InvalidIdentifierException;
import pt.tecnico.sauron.silo.exceptions.NoObservationException;
import pt.tecnico.sauron.silo.exceptions.DuplicateCameraException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraNameException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraCoordinatesException;

public class Sauron {
  private List<Observation> observations = new ArrayList<Observation>();
  private List<Camera> cameras = new ArrayList<Camera>();

  public PingInformation ctrlPing() {
    PingInformation information = new PingInformation(cameras, observations);
    System.out.println("The system Silo was pinged!");

    return information;
  }

  public void ctrlClear() {
    observations = new ArrayList<Observation>();
    cameras = new ArrayList<Camera>();
  }

  public void ctrlInit() {
    // TODO
  }

  public void addCamera(String name, Float latitude, Float longitude) throws DuplicateCameraException, InvalidCameraNameException, InvalidCameraCoordinatesException {
    try {
      Camera cam = getCamera(name);
      if (!(cam.getName() == name && cam.getCoordinates().getLatitude() == latitude && cam.getCoordinates().getLongitude() == longitude)){
        throw new DuplicateCameraException(name);
      }
    } catch (InvalidCameraException e) {
      Coordinates coordinates = new Coordinates(latitude, longitude);
      Camera camera = new Camera(name, coordinates);
      cameras.add(camera);
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
      .filter(observation -> type == observation.getType() && identifier == observation.getIdentifier())
      .max(Comparator.comparing(Observation::getDatetime))
      .orElse(null);

    if (lastObservation == null) {
      throw new NoObservationException(identifier);
    }

    return lastObservation;
  }

  public List<Observation> trackMatch(ObservationType type, String pattern) throws NoObservationException {

    /* TODO String patternRegex = "^".concat(pattern).concat("$");
    if (type == ObservationType.PERSON) {
      patternRegex.replace("*", "\\d*");
    } else if (type == ObservationType.CAR) {
      patternRegex.replace("*", "\\w*");
    }

    Observation lastObservation = observations.stream()
    .filter(observation -> type == observation.getType() && observation.getIdentifier().matches(patternRegex))
    .max(Comparator.comparing(Observation::getDatetime))
    .orElse(null);

    if (lastObservation == null)
    throw new NoObservationException(pattern);

    return lastObservation; */

    return new ArrayList<Observation>();
  }

  public List<Observation> trace(ObservationType type, String identifier) throws NoObservationException {
    List<Observation> observationsMatch = observations.stream()
      .filter(observation -> type == observation.getType() && identifier == observation.getIdentifier())
      .sorted(Comparator.comparing(Observation::getDatetime))
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
  }
}
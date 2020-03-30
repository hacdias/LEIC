package pt.tecnico.sauron.silo.client.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import com.google.protobuf.Timestamp;

import pt.tecnico.sauron.silo.grpc.Silo;

public class Observation {
  private ObservationType type;
  private String identifier;
  private LocalDateTime datetime;
  private Camera camera;

  private final Map<Silo.ObservationType, ObservationType> typesConverter = Map.ofEntries(
    Map.entry(Silo.ObservationType.PERSON, ObservationType.PERSON),
    Map.entry(Silo.ObservationType.CAR, ObservationType.CAR));

  public Observation(Camera camera, ObservationType type, String identifier, LocalDateTime datetime) {
    this.type = type;
    this.identifier = identifier;
    this.datetime = datetime;
    this.camera = camera;
  }

  public Observation(Silo.Camera camera, Silo.Observation observation) {
    this.type = typesConverter.get(observation.getType());
    this.identifier = observation.getIdentifier();
    Timestamp ts = observation.getTimestamp();
    this.datetime = Instant
      .ofEpochSecond(ts.getSeconds() , ts.getNanos())
      .atZone(ZoneId.systemDefault())
      .toLocalDateTime();
    this.camera = new Camera(camera);
  }

  /**
   * @return the type
   */
  public ObservationType getType() {
    return type;
  }

  /**
   * @return the datetime
   */
  public LocalDateTime getDatetime() {
    return datetime;
  }

  /**
   * @return the identifier
   */
  public String getIdentifier() {
    return identifier;
  }

  /**
   * @return the camera
   */
  public Camera getCamera() {
    return camera;
  }

  @Override
  public String toString() {
    return "Observation(" + type + "," +identifier + ")@" + datetime.toString() + " by " + camera.toString();
  }
}
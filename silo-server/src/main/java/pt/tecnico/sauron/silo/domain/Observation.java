package pt.tecnico.sauron.silo.domain;

import java.time.LocalDateTime;
import pt.tecnico.sauron.silo.exceptions.InvalidIdentifierException;

public class Observation {
  private ObservationType type;
  private String identifier;
  private LocalDateTime datetime;
  private Camera camera;

  public Observation(Camera camera, ObservationType type, String identifier, LocalDateTime datetime) throws InvalidIdentifierException {
    this.type = type;
    this.identifier = identifier;
    this.datetime = datetime;
    this.camera = camera;

    if ((type == ObservationType.CAR && !identifier.matches("^(\\d{2}|\\w{2}){3}$")) ||
      (type == ObservationType.PERSON && !identifier.matches("^\\d+$"))) {
      throw new InvalidIdentifierException(identifier);
    }
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
}
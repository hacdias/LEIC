package pt.tecnico.sauron.silo.domain;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    if ((type == ObservationType.CAR && !this.checkCarIdentifier(identifier)) ||
      (type == ObservationType.PERSON && !identifier.matches("^\\d+$"))) {
      throw new InvalidIdentifierException(identifier);
    }
  }

  private Boolean checkCarIdentifier(String identifier) {
    Integer charGroups = new PatternMatcher("[A-Z]{2}").matches(identifier);
    Integer intGroups = new PatternMatcher("[0-9]{2}").matches(identifier);

    return charGroups >= 1 && intGroups >= 1 && intGroups + charGroups == 3;
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

  private static class PatternMatcher {
    Pattern pattern;

    PatternMatcher(String regex) {
      this.pattern = Pattern.compile(regex);
    }

    public Integer matches(String string) {
      Matcher matcher = pattern.matcher(string);
      Integer count = 0;
      while (matcher.find()) {
        count++;
      }
      return count;
    }
  }
}
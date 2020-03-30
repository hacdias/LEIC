package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.InvalidCameraNameException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraCoordinatesException;

public class Camera {
  private String name;
  private Coordinates coordinates;

  public Camera(String name, Coordinates coordinates) throws InvalidCameraNameException, InvalidCameraCoordinatesException {
    this.name = name;
    this.coordinates = coordinates;

    if(!(name.matches("^(\\d|\\w){*}$")||name.length() > 3 || name.length()< 15)){
      throw new InvalidCameraNameException(name);
    }
    if (!((coordinates.getLatitude() > -90 && coordinates.getLatitude() < 90) || ( coordinates.getLongitude() > -180 && coordinates.getLongitude() < 180))){
      throw new InvalidCameraCoordinatesException(coordinates);
    }
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the coordinates
   */
  public Coordinates getCoordinates() {
    return coordinates;
  }
}

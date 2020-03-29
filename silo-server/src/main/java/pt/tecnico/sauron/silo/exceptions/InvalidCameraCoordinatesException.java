package pt.tecnico.sauron.silo.exceptions;

import pt.tecnico.sauron.silo.domain.Coordinates;

public class InvalidCameraCoordinatesException extends Exception {
  private static final long serialVersionUID = 1L;
  Coordinates camCoordinates;

  public InvalidCameraCoordinatesException(Coordinates coordinates) {
    camCoordinates = coordinates;
  }

  public String toString(){
    return ("InvalidCameraCoordinatesException Occurred: " + camCoordinates) ;
  }

  /**
   * @return the coordinates
   */
  public Coordinates getCamCoordinates() {
    return camCoordinates;
  }
}
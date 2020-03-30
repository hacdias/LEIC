package pt.tecnico.sauron.silo.client.exceptions;

public class InvalidCameraCoordinatesException extends SauronClientException {
  private static final long serialVersionUID = 9200692143824563723L;

  public InvalidCameraCoordinatesException() {
  }

  public String toString(){
    return ("InvalidCameraCoordinatesException Occurred") ;
  }
}

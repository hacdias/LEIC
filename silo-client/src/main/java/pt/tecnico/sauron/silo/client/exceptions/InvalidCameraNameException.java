package pt.tecnico.sauron.silo.client.exceptions;

public class InvalidCameraNameException extends SauronClientException {
  private static final long serialVersionUID = 9200692143826563723L;

  public InvalidCameraNameException() {
  }

  public String toString(){
    return ("InvalidCameraNameException Occurred") ;
  }
}

package pt.tecnico.sauron.silo.client.exceptions;

public class NoObservationFoundException extends SauronClientException {
  private static final long serialVersionUID = 9200692144824563723L;

  public NoObservationFoundException() {
  }

  public String toString(){
    return ("NoObservationFoundException Occurred") ;
  }
}

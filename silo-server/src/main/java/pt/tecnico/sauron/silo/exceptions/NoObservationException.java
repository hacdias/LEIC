package pt.tecnico.sauron.silo.exceptions;

public class NoObservationException extends Exception {
	private static final long serialVersionUID = 2901115731432827915L;
  String identifier;

  public NoObservationException(String id) {
    identifier = id;
  }

  public String toString(){
    return ("NoObservationException Occurred: " + identifier) ;
  }

  /**
   * @return the identifier
   */
  public String getIdentifier() {
    return identifier;
  }
}

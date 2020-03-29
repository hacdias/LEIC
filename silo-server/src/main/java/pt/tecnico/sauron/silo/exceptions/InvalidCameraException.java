package pt.tecnico.sauron.silo.exceptions;

public class InvalidCameraException extends Exception {
  private static final long serialVersionUID = 9200692101826563723L;
  String identifier;

  public InvalidCameraException(String id) {
    identifier = id;
  }

  public String toString(){
    return ("InvalidCameraException Occurred: " + identifier) ;
  }

  /**
   * @return the identifier
   */
  public String getIdentifier() {
    return identifier;
  }
}

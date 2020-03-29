package pt.tecnico.sauron.silo.exceptions;

public class DuplicateCameraException extends Exception {
  private static final long serialVersionUID = 9200692141826563723L;
  String identifier;

  public DuplicateCameraException(String id) {
    identifier = id;
  }

  public String toString(){
    return ("DuplicateCameraException Occurred: " + identifier) ;
  }

  /**
   * @return the identifier
   */
  public String getIdentifier() {
    return identifier;
  }
}

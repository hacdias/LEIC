package pt.tecnico.sauron.silo.exceptions;

public class InvalidCameraNameException extends Exception {
    private static final long serialVersionUID = 5553957057209885665L;
    String camName;

  public InvalidCameraNameException(String name) {
    camName = name;
  }

  public String toString(){
    return ("InvalidCameraNameException Occurred: " + camName) ;
  }

  /**
   * @return the name
   */
  public String getCamName() {
    return camName;
  }
}
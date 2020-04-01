package pt.tecnico.sauron.silo.exceptions;

public class InvalidCameraCoordinatesException extends Exception {
  private static final long serialVersionUID = 1L;
  Double latitude;
  Double longitude;

  public InvalidCameraCoordinatesException(Double latitude, Double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public String toString(){
    return ("InvalidCameraCoordinatesException Occurred: " + latitude + ", " + longitude) ;
  }

  /**
   * @return the latitude
   */
  public Double getLatitude() {
    return latitude;
  }

  /**
   * @return the longitude
   */
  public Double getLongitude() {
    return longitude;
  }
}
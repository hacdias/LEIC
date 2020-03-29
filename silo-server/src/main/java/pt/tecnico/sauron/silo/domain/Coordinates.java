package pt.tecnico.sauron.silo.domain;

public class Coordinates {
  private Float latitude;
  private Float longitude;

  public Coordinates(Float latitude, Float longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * @return the latitude
   */
  public Float getLatitude() {
    return latitude;
  }

  /**
   * @return the longitude
   */
  public Float getLongitude() {
    return longitude;
  }
}
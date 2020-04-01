package pt.tecnico.sauron.silo.domain;

public class Coordinates {
  private Double latitude;
  private Double longitude;

  public Coordinates(Double latitude, Double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
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

  @Override
  public String toString() {
    return "Coordinates(" + latitude + "," + longitude + ")";
  }
}
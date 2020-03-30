package pt.tecnico.sauron.silo.client.domain;

import pt.tecnico.sauron.silo.grpc.Silo;

public class Coordinates {
  private Float latitude;
  private Float longitude;

  public Coordinates(Float latitude, Float longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public Coordinates(Silo.Coordinates coordinates) {
    this.latitude = coordinates.getLatitude();
    this.longitude = coordinates.getLongitude();
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

  @Override
  public String toString() {
    return "Coordinates(" + latitude + "," + longitude + ")";
  }
}
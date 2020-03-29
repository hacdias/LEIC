package pt.tecnico.sauron.silo.domain;

public class Camera {
  private String name;
  private Coordinates coordinates;

  public Camera(String name, Coordinates coordinates) {
    this.name = name;
    this.coordinates = coordinates;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the coordinates
   */
  public Coordinates getCoordinates() {
    return coordinates;
  }
}

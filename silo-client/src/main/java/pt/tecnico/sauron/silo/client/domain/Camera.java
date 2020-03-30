package pt.tecnico.sauron.silo.client.domain;

import pt.tecnico.sauron.silo.grpc.Silo;

public class Camera {
  private String name;
  private Coordinates coordinates;

  public Camera(String name, Coordinates coordinates) {
    this.name = name;
    this.coordinates = coordinates;
  }

  public Camera(Silo.Camera camera) {
    this.name = camera.getName();
    this.coordinates = new Coordinates(camera.getCoordinates());
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

  @Override
  public String toString() {
    return "Camera(" + name + ")@" + coordinates.toString();
  }
}

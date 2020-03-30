package pt.tecnico.sauron.silo.domain;

import java.util.List;

public class PingInformation {
  private List<Camera> cameras;
  private List<Observation> observations;

  public PingInformation(List<Camera> cameras, List<Observation> observations) {
    this.cameras = cameras;
    this.observations = observations;
  }

  /**
   * @return the cameras
   */
  public List<Camera> getCameras() {
    return cameras;
  }

  /**
   * @return the observations
   */
  public List<Observation> getObservations() {
    return observations;
  }
}

package pt.tecnico.sauron.silo.domain;

public class PingInformation {
  private Integer camerasNumber;
  private Integer observationsNumber;

  public PingInformation(Integer cameras, Integer observations) {
    this.camerasNumber = cameras;
    this.observationsNumber = observations;
  }

  /**
   * @return the number of cameras
   */
  public Integer getCamerasNumber() {
    return camerasNumber;
  }

  /**
   * @return the number of observations
   */
  public Integer getObservationsNumber() {
    return observationsNumber;
  }
}

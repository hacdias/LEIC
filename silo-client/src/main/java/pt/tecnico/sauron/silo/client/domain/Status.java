package pt.tecnico.sauron.silo.client.domain;

import java.util.List;

public class Status {
  List<Camera> cameras;
  List<Observation> observations;

  public Status(List<Camera> cameras, List<Observation> observations) {
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

  @Override
  public String toString() {
    String info = "Status(\n\tCameras(";

    for (Integer i = 0; i < cameras.size(); i++) {
      info += "\n\t\t" + cameras.get(i).toString();
      if (i != cameras.size() - 1) info += ",";
    }

    info += "\n\t),\n\tObservations(";

    for (Integer i = 0; i < observations.size(); i++) {
      info += "\n\t\t" + observations.get(i).toString();
      if (i != observations.size() - 1) info += ",";
    }

    return info + "\n\t)\n)";
  }
}

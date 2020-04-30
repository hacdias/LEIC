package pt.tecnico.sauron.silo.client.domain;

import java.util.List;

public class Status {
    List<Camera> cameras;
    List<Observation> observations;

    public Status(List<Camera> cameras, List<Observation> observations) {
        this.cameras = cameras;
        this.observations = observations;
    }

    public List<Camera> getCameras() {
        return cameras;
    }

    public List<Observation> getObservations() {
        return observations;
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder("Status(\n\tCameras(");

        for (int i = 0; i < cameras.size(); i++) {
            info.append("\n\t\t").append(cameras.get(i).toString());
            if (i != cameras.size() - 1) info.append(",");
        }

        info.append("\n\t),\n\tObservations(");

        for (int i = 0; i < observations.size(); i++) {
            info.append("\n\t\t").append(observations.get(i).toString());
            if (i != observations.size() - 1) info.append(",");
        }

        return info.toString() + "\n\t)\n)";
    }
}

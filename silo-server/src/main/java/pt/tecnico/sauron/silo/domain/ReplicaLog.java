package pt.tecnico.sauron.silo.domain;

import java.util.ArrayList;
import java.util.List;

public class ReplicaLog {
    private List<Integer> timestamp;
    private Camera camera = null;
    private List<Observation> observations = null;

    public ReplicaLog(List<Integer> timestamp, Camera camera) {
        this.timestamp = new ArrayList<>(timestamp);
        this.camera = camera;
    }

    public ReplicaLog(List<Integer> timestamp, List<Observation> observations) {
        this.timestamp = new ArrayList<>(timestamp);
        this.observations = new ArrayList<>(observations);
    }

    public List<Integer> getTimestamp() {
        return timestamp;
    }

    public Camera getCamera() {
        return camera;
    }

    public List<Observation> getObservations() {
        return observations;
    }
}

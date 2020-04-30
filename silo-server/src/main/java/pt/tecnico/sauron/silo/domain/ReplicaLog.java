package pt.tecnico.sauron.silo.domain;

import java.util.ArrayList;
import java.util.List;

public class ReplicaLog {
    private List<Integer> timestamp;
    private String uuid;
    private Camera camera = null;
    private List<Observation> observations = null;

    public ReplicaLog(List<Integer> timestamp, String uuid, Camera camera) {
        this.timestamp = new ArrayList<>(timestamp);
        this.camera = camera;
        this.uuid = uuid;
    }

    public ReplicaLog(List<Integer> timestamp, String uuid, List<Observation> observations) {
        this.timestamp = new ArrayList<>(timestamp);
        this.observations = new ArrayList<>(observations);
        this.uuid = uuid;
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

    public String getUuid() {
        return uuid;
    }
}

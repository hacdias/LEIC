package pt.tecnico.sauron.silo.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplicaResponse {
    private List<Integer> timestamp;
    private Observation observation;
    private Camera camera;
    private Map<String,Camera> cameras = new HashMap<>();
    private List<Observation> observations = new ArrayList<>();

    public ReplicaResponse(List<Integer> timestamp) {
        this.timestamp = new ArrayList<>(timestamp);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setObservation(Observation observation) {
        this.observation = observation;
    }

    public void setCameras(Map<String,Camera> cameras) {
        this.cameras = new HashMap<>(cameras);
    }

    public void setObservations(List<Observation> observations) {
        this.observations = new ArrayList<>(observations);
    }

    public List<Integer> getTimestamp() {
        return timestamp;
    }

    public List<Observation> getObservations() {
        return observations;
    }

    public Camera getCamera() {
        return camera;
    }

    public Map<String, Camera> getCameras() {
        return cameras;
    }

    public Observation getObservation() {
        return observation;
    }
}

package pt.tecnico.sauron.silo.domain;

import java.util.ArrayList;
import java.util.List;

public class ReplicaResponse {
    private List<Integer> timestamp;
    private Observation observation;
    private Camera camera;
    private List<Camera> cameras = new ArrayList<>();
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

    public void setCameras(List<Camera> cameras) {
        this.cameras = new ArrayList<>(cameras);
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

    public List<Camera> getCameras() {
        return cameras;
    }

    public Observation getObservation() {
        return observation;
    }
}

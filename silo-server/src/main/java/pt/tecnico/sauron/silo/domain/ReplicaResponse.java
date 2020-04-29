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

    public ReplicaResponse(List<Integer> timestamp, Observation observation) {
        this(timestamp);
        this.observation = observation;
    }

    public ReplicaResponse(List<Integer> timestamp, Camera camera) {
        this(timestamp);
        this.camera = camera;
    }

    @SuppressWarnings("unchecked")
    public ReplicaResponse(List<Integer> timestamp, List<?> objects) {
        this.timestamp = timestamp;

        if (objects.isEmpty()) {
            return;
        }

        // We make a copy of the list to assure it is not modified, i.e.,
        // no new elements are added and it represents the actual state when
        // this was created.

        if (objects.get(0) instanceof Camera) {
            this.cameras = new ArrayList<Camera>((List<Camera>)objects);
            return;
        }

        if (objects.get(0) instanceof Observation) {
            this.observations = new ArrayList<Observation>((List<Observation>)objects);
            return;
        }

        throw new IllegalArgumentException();
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

package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.grpc.Silo;

import java.util.ArrayList;
import java.util.List;

public class ReplicaLog {
    private List<Integer> timestamp;
    private Camera camera = null;
    private Observation observation = null;

    public ReplicaLog(List<Integer> timestamp, Camera camera) {
        this.timestamp = new ArrayList<Integer>(timestamp);
        this.camera = camera;
    }

    public ReplicaLog(List<Integer> timestamp, Observation observation) {
        this.timestamp = new ArrayList<Integer>(timestamp);
        this.observation = observation;
    }

    public List<Integer> getTimestamp() {
        return timestamp;
    }

    public Camera getCamera() {
        return camera;
    }

    public Observation getObservation() {
        return observation;
    }
}

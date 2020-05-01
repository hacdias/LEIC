package pt.tecnico.sauron.silo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReplicaLog implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Integer instance;
    private final List<Integer> prev;
    private final List<Integer> timestamp;
    private final String uuid;
    private final Camera camera;
    private final List<Observation> observations;

    public ReplicaLog(Integer instance, List<Integer> prev, List<Integer> timestamp, String uuid, Camera camera) {
        this.instance = instance;
        this.timestamp = new ArrayList<>(timestamp);
        this.uuid = uuid;
        this.prev = new ArrayList<>(prev);

        this.camera = camera;
        this.observations = null;
    }

    public ReplicaLog(Integer instance, List<Integer> prev, List<Integer> timestamp, String uuid, List<Observation> observations) {
        this.instance = instance;
        this.timestamp = new ArrayList<>(timestamp);
        this.uuid = uuid;
        this.prev = new ArrayList<>(prev);

        this.camera = null;
        this.observations = new ArrayList<>(observations);
    }

    public Integer getInstance() {
        return instance;
    }

    public List<Integer> getTimestamp() {
        return timestamp;
    }

    public List<Integer> getPrev() {
        return prev;
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

    @Override
    public String toString() {
        return "ReplicaLog{" +
            "instance=" + instance +
            ", prev=" + prev +
            ", timestamp=" + timestamp +
            ", uuid='" + uuid + '\'' +
            ", camera=" + (camera == null ? "NO CAM " : camera.toString()) +
            ", observations=" + (observations == null ? "NO OBS": observations.toString()) +
            '}';
    }
}

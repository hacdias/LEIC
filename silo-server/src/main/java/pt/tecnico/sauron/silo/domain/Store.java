package pt.tecnico.sauron.silo.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Store implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final List<Integer> valueTimestamp;
    private final List<Observation> observations;
    private final Map<String, Camera> cameras;
    private final List<Integer> replicaTimestamp;
    private final List<ReplicaLog> log;
    private final List<String> operationsTable;

    public Store(List<Integer> valueTimestamp, List<Observation> observations, Map<String, Camera> cameras, List<Integer> replicaTimestamp, List<ReplicaLog> log, List<String> operationsTable) {
        this.valueTimestamp = valueTimestamp;
        this.observations = observations;
        this.cameras = cameras;
        this.replicaTimestamp = replicaTimestamp;
        this.log = log;
        this.operationsTable = operationsTable;
    }

    public List<Integer> getValueTimestamp() {
        return valueTimestamp;
    }

    public List<Observation> getObservations() {
        return observations;
    }

    public List<Integer> getReplicaTimestamp() {
        return replicaTimestamp;
    }

    public List<ReplicaLog> getLog() {
        return log;
    }

    public List<String> getOperationsTable() {
        return operationsTable;
    }

    public Map<String, Camera> getCameras() {
        return cameras;
    }
}
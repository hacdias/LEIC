package pt.tecnico.sauron.silo.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO:
//  every 30 seconds (default, can be customized), send our information to other replicas
//  For this, I think we might even use Silo-Frontend to keep connections to all other replicas.
//  - Check book on how to estimate which changes the other replicas DON'T have.
//  - Changes must be sent in order.

public class ReplicaManager {
  private final Integer instance;

  // By using a static object as a lock, we can safely use this as a synchronized
  // lock between multiple threads instead of locking on each object.
  private static final Object lock = new Object();

  // This is the data we want to keep in sync with other replicas.
  private final List<Integer> valueTimestamp = Collections.synchronizedList(new ArrayList<>());
  private final List<Observation> observations = Collections.synchronizedList(new ArrayList<>());
  private final List<Camera> cameras = Collections.synchronizedList(new ArrayList<>());

  // The update information!
  private final List<Integer> replicaTimestamp = Collections.synchronizedList(new ArrayList<>());
  private final List<ReplicaLog> log = Collections.synchronizedList(new ArrayList<>());
  private final List<List<Integer>> operationsTable = Collections.synchronizedList(new ArrayList<>());

  public ReplicaManager(Integer instance, Integer numberServers) {
    this.instance = instance;

    for (int i = 0; i < numberServers; i++) {
      this.replicaTimestamp.add(0);
      this.valueTimestamp.add(0);
    }
  }

  public ReplicaResponse addObservation (List<Integer> prev, Observation observation) {
    return add(prev, observation);
  }

  public ReplicaResponse addCamera (List<Integer> prev, Camera camera) {
    return add(prev, camera);
  }

  public ReplicaResponse getObservations(List<Integer> prev) {
    return get(prev, false, true);
  }

  public ReplicaResponse getCameras (List<Integer> prev) {
    return get(prev, true, false);
  }

  public ReplicaResponse getAll (List<Integer> prev) {
    return get(prev, true, true);
  }

  private void incrementReplicaTimestamp () {
    this.replicaTimestamp.set(this.instance -1, this.replicaTimestamp.get(this.instance -1) + 1);
  }

  private void updatePrevTimestamp (List<Integer> prev) {
    prev.set(this.instance - 1, this.replicaTimestamp.get(this.instance - 1));
  }

  private ReplicaResponse add (List<Integer> prev, Object o) {
    synchronized (lock) {
      // Discard if already done...
      if (this.operationsTable.contains(prev)) {
        return new ReplicaResponse(prev);
      }

      incrementReplicaTimestamp();
      updatePrevTimestamp(prev);

      if (o instanceof Camera) {
        log.add(new ReplicaLog(prev, (Camera)o));
      } else if (o instanceof Observation) {
        log.add(new ReplicaLog(prev, (Observation)o));
      }
    }

    new Thread(() -> {
      while (true) {
        synchronized (lock) {
          if (validTimestamp(prev)) {
            // TODO: execute operation and update valueTimestamp:
            //  - For each entry i, update valueTimestamp[i] if replicaTimestamp[i] > valueTimestamp[i]
            return;
          }
        }
      }
    }).start();

    return new ReplicaResponse(prev);
  }

  private ReplicaResponse get (List<Integer> prev, boolean isCameras, boolean isObservations) {
    while (true) {
      synchronized (lock) {
        if (validTimestamp(prev)) {
          if (isCameras && !isObservations) {
            return new ReplicaResponse(this.valueTimestamp, cameras);
          } else if (isObservations && !isCameras) {
            return new ReplicaResponse(this.valueTimestamp, observations);
          } else {
            return new ReplicaResponse(this.valueTimestamp, cameras, observations);
          }
        }
      }

      try {
        Thread.sleep(100);
      } catch (InterruptedException ignored) {}
    }
  }

  // NOTE: this function must only be called in synchronized blocks!
  private boolean validTimestamp (List<Integer> prev) {
    for (int i = 0; i < prev.size(); i++) {
      if (prev.get(i) > this.valueTimestamp.get(i)) {
        return false;
      }
    }

    return true;
  }
}
package pt.tecnico.sauron.silo.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class ReplicaManager {
    private final Integer instance;
    private final Integer numberServers;

    private static final Logger LOGGER = Logger.getLogger(ReplicaManager.class.getName());

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
    private final List<String> operationsTable = Collections.synchronizedList(new ArrayList<>());

    List<Thread> threads = Collections.synchronizedList(new ArrayList<>());

    public ReplicaManager(Integer instance, Integer numberServers) {
        LOGGER.info(String.format("Replica manager created: instance %d; numberServers: %d", instance, numberServers));

        this.instance = instance;
        this.numberServers = numberServers;

        for (int i = 0; i < numberServers; i++) {
            this.replicaTimestamp.add(0);
            this.valueTimestamp.add(0);
        }

        threads.add(update(1000 * 30));
        threads.get(0).start();
    }

    private Thread update (Integer milliseconds) {
        return new Thread(() -> {
            while (true)  {
                LOGGER.info("UPDATE THREAD");
                // TODO:
                //  send our information to other replicas, for this, I think we might even use Silo-Frontend to keep
                //  connections to all other replicas.
                //  - Check book on how to estimate which changes the other replicas DON'T have.
                //  - Changes must be sent in order.

                try {
                    Thread.sleep(milliseconds);
                } catch (InterruptedException e) {
                    LOGGER.info("Thread interrupted.");
                    return;
                }
            }
        });
    }

    public ReplicaResponse addObservations (List<Integer> prev, String uuid, List<Observation> observations) {
        ReplicaResponse res = add(prev, uuid, (List<Integer> timestamp) -> {
            LOGGER.info(String.format("Adding observations to log: %h", observations));
            log.add(new ReplicaLog(timestamp, uuid, observations));
        }, () -> {
            if (observations.isEmpty()) {
                LOGGER.info("Observations list is empty, skipping.");
                return;
            }

            String cameraName = observations.get(0).getCameraName();
            Camera camera = null;

            for (Camera cam : cameras) {
                if (cam.getName().equals(cameraName)) {
                    camera = cam;
                    break;
                }
            }

            if (camera == null) {
                LOGGER.info("Camera " + cameraName + "does not exist. THIS SHOULD NOT HAPPEN");
                return;
            }

            for (Observation observation : observations) {
                observation.setCamera(camera);
                this.observations.add(observation);
                LOGGER.info("Observation added to the stable value: " + observation.toString());
            }
        });

        res.setObservations(observations);
        return res;
    }

    public ReplicaResponse addCamera (List<Integer> prev, String uuid, Camera camera) {
        ReplicaResponse res = add(prev, uuid, (List<Integer> timestamp) -> {
            LOGGER.info("Adding camera to log: " + camera.toString());
            log.add(new ReplicaLog(timestamp, uuid, camera));
        }, () -> {
            this.cameras.add(camera);
            LOGGER.info("Camera added to the stable value: " + camera.toString());
        });

        res.setCamera(camera);
        return res;
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

    private List<Integer> parsePrev (List<Integer> prev) {
        if (prev == null || prev.size() != numberServers) {
            return new ArrayList<>(valueTimestamp);
        }

        return new ArrayList<>(prev);
    }

    private ReplicaResponse add (List<Integer> prev, String uuid, AppendFunction append, ExecuteFunction execute) {
        List<Integer> newTimestamp = parsePrev(prev);

        synchronized (lock) {
            // Discard if already done...
            if (this.operationsTable.contains(uuid)) {
                LOGGER.info("Operation already done: " + uuid);
                return new ReplicaResponse(prev);
            }

            incrementReplicaTimestamp();
            updatePrevTimestamp(newTimestamp);
            LOGGER.info("Timestamp " + prev.toString() + " updated to " + newTimestamp.toString());
            append.run(newTimestamp);

            Thread thread = new Thread(() -> {
                while (true) {
                    synchronized (lock) {
                        if (validTimestamp(prev)) {
                            LOGGER.info("Will execute (add) " + newTimestamp.toString());
                            execute.run();
                            operationsTable.add(uuid);

                            for (int i = 0; i < newTimestamp.size(); i++) {
                                if (replicaTimestamp.get(i) > valueTimestamp.get(i)) {
                                    valueTimestamp.set(i, replicaTimestamp.get(i));
                                }
                            }

                            LOGGER.info("New value timestamp is: " + valueTimestamp.toString());
                            return;
                        }
                    }
                }
            });

            threads.add(thread);
            thread.start();
        }

        return new ReplicaResponse(newTimestamp);
    }

    private ReplicaResponse get (List<Integer> rawPrev, boolean isCameras, boolean isObservations) {
        List<Integer> prev = parsePrev(rawPrev);
        LOGGER.info("Pending execute (get) " + prev.toString());

        while (true) {
            synchronized (lock) {
                if (validTimestamp(prev)) {
                    LOGGER.info("Will execute (get) " + prev.toString());
                    ReplicaResponse res = new ReplicaResponse(prev);
                    if (isCameras) {
                        res.setCameras(cameras);
                    }

                    if (isObservations) {
                        res.setObservations(observations);
                    }

                    return res;
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
                LOGGER.info("Thread interrupted.");
                return null;
            }
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

    private interface AppendFunction {
        void run(List<Integer> timestamp);
    }

    private interface ExecuteFunction {
        void run();
    }

    public void close () {
        synchronized (lock) {
            for (Thread thread : threads) {
                thread.interrupt();
                try {
                    thread.join();
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
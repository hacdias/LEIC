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

    private final List<List<Integer>> tableTimestamps;

    List<Thread> threads = Collections.synchronizedList(new ArrayList<>());

    public ReplicaManager(Integer instance, Integer numberServers, String host, Integer basePort) {
        LOGGER.info(String.format("Replica manager created: instance %d; numberServers: %d", instance, numberServers));

        this.instance = instance - 1;
        this.numberServers = numberServers;
        this.tableTimestamps = new ArrayList<>();

        for (int i = 0; i < numberServers; i++) {
            this.replicaTimestamp.add(0);
            this.valueTimestamp.add(0);
        }

        for (int i = 0; i < numberServers; i++) {
            this.tableTimestamps.add(new ArrayList<>(this.replicaTimestamp));
        }

        this.tableTimestamps.set(this.instance, this.replicaTimestamp);
        this.threads.add(update(1000 * 30, numberServers, host, basePort + 1));
        this.threads.get(0).start();
    }

    private Thread update (Integer milliseconds, Integer numberServers, String host, Integer basePort) {
        return new Thread(() -> {
            List<Replica> replicas = new ArrayList<>();

            for (int i = 0; i < numberServers; i++) {
                if (i == this.instance) {
                    continue;
                }
                replicas.add(new Replica(this.instance, i, host + ":" + Integer.toString(basePort + i)));
            }

            while (true)  {
                LOGGER.info("UPDATE THREAD");

                for (Replica replica : replicas) {
                    replica.gossip(this.log, this.replicaTimestamp);
                }

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
        ReplicaResponse res = add(prev, uuid, null, observations);
        res.setObservations(observations);
        return res;
    }

    public ReplicaResponse addCamera (List<Integer> prev, String uuid, Camera camera) {
        ReplicaResponse res = add(prev, uuid, camera, null);
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

    public void receiveGossip (List<Integer> sourceTimestamp, Integer sourceInstance, List<ReplicaLog> receivedLog) {
        synchronized (lock) {
            LOGGER.info("Processing gossips... ");

            tableTimestamps.set(sourceInstance, sourceTimestamp);

            if (receivedLog.isEmpty()) {
                LOGGER.info("Gossip was empty... how dare you?");
            }

            for (ReplicaLog r : receivedLog) {
                if (this.operationsTable.contains(r.getUuid())) {
                    LOGGER.info("Operation already processed (UUID on operations table): " + r.getUuid());
                    continue;
                }

                boolean add = false;
                for (int i = 0; i < numberServers; i++) {
                    if (r.getTimestamp().get(i) > this.replicaTimestamp.get(i)) {
                        add = true;
                        break;
                    }
                }

                if (add) {
                    this.log.add(r);
                    LOGGER.info("Operation added to log: " + r.getUuid());
                } else {
                    LOGGER.info("Operation already processed (r.ts < replicaTS): " + r.getUuid());
                }
            }

            for (int i = 0; i < numberServers; i++) {
                if (sourceTimestamp.get(i) > this.replicaTimestamp.get(i)) {
                    this.replicaTimestamp.set(i, sourceTimestamp.get(i));
                }
            }

            goThroughLog();
            cleanupLog();
            LOGGER.info("Gossip processed.");
        }
    }

    private void cleanupLog () {
        LOGGER.info("Cleaning up logs...");
        List<ReplicaLog> toRemove = new ArrayList<>();

        for (int i = 0; i < numberServers; i++) {
            System.out.println(tableTimestamps.get(i).toString());
        }

        System.out.println("LOGS;::::::");

        for (ReplicaLog r : this.log) {
            int c = r.getInstance();

            boolean canRemove = true;

            System.out.println(r.getTimestamp().toString());

            for (int i = 0; i < numberServers; i++) {
                if (tableTimestamps.get(i).get(c) < r.getTimestamp().get(c)) {
                    canRemove = false;
                }
            }

            if (canRemove) toRemove.add(r);
        }

        for (ReplicaLog r : toRemove) {
            LOGGER.info("Removed log: " + r.getUuid());
            this.log.remove(r);
        }
    }

    private List<Integer> parsePrev (List<Integer> prev) {
        if (prev == null || prev.size() != numberServers) {
            return new ArrayList<>(valueTimestamp);
        }

        return new ArrayList<>(prev);
    }

    private void goThroughLog () {
        sortLog();

        for (ReplicaLog r : this.log) {
            execute(r);
        }
    }

    private void execute(ReplicaLog r) {
        if (r == null) {
            return;
        }

        if (validTimestamp(r.getPrev()) && !this.operationsTable.contains(r.getUuid())) {
            LOGGER.info("Will execute (add) " + r.getUuid());
            executeAux(r);
            operationsTable.add(r.getUuid());

            for (int i = 0; i < numberServers; i++) {
                if (r.getTimestamp().get(i) > valueTimestamp.get(i)) {
                    valueTimestamp.set(i, r.getTimestamp().get(i));
                }
            }

            LOGGER.info("Executed (add) " + r.getUuid());
        }
    }

    private void executeAux (ReplicaLog r) {
        if (r.getCamera() != null) {
            this.cameras.add(r.getCamera());
            LOGGER.info("Camera added to the stable value: " + r.getCamera().toString());
        } else if (r.getObservations() != null) {
            if (r.getObservations().isEmpty()) {
                LOGGER.info("Observations list is empty, skipping.");
                return;
            }

            String cameraName = r.getObservations().get(0).getCameraName();
            Camera camera = null;

            for (Camera cam : cameras) {
                if (cam.getName().equals(cameraName)) {
                    camera = cam;
                    break;
                }
            }

            if (camera == null) {
                LOGGER.severe("Camera " + cameraName + "does not exist. THIS SHOULD NOT HAPPEN");
                return;
            }

            for (Observation observation : r.getObservations()) {
                observation.setCamera(camera);
                this.observations.add(observation);
                LOGGER.info("Observation added to the stable value: " + observation.toString());
            }
        }
    }

    private ReplicaResponse add (List<Integer> prev, String uuid, Camera camera, List<Observation> observations) {
        List<Integer> newTimestamp = parsePrev(prev);

        synchronized (lock) {
            // Discard if already done...
            if (this.operationsTable.contains(uuid)) {
                LOGGER.info("Operation already done: " + uuid);
                return new ReplicaResponse(prev);
            }

            for (ReplicaLog r : this.log) {
                if (r.getUuid().equals(uuid)) {
                    LOGGER.info("Operation already in log: " + uuid);
                    return new ReplicaResponse(prev);
                }
            }

            this.replicaTimestamp.set(this.instance, this.replicaTimestamp.get(this.instance) + 1);
            newTimestamp.set(this.instance, this.replicaTimestamp.get(this.instance));

            ReplicaLog r = null;

            if (camera != null) {
                LOGGER.info("Adding camera to log: " + uuid);
                r = new ReplicaLog(this.instance, prev, newTimestamp, uuid, camera);
                log.add(r);
            } else if (observations != null) {
                LOGGER.info("Adding observations to log" + uuid);
                r = new ReplicaLog(this.instance, prev, newTimestamp, uuid, observations);
                log.add(r);
            }

            execute(r);
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
                    ReplicaResponse res = new ReplicaResponse(valueTimestamp);
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

    private void sortLog () {
        log.sort((r1, r2) -> {
            for (int i = 0; i < this.numberServers; i++) {
                if (r1.getPrev().get(i) < r2.getPrev().get(i)) {
                    return -1;
                }
            }

            return 1;
        });
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
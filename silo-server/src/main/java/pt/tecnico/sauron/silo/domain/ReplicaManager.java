package pt.tecnico.sauron.silo.domain;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class ReplicaManager {
    private final Options options;

    private static final Logger LOGGER = Logger.getLogger(ReplicaManager.class.getName());

    // By using a static object as a lock, we can safely use this as a synchronized
    // lock between multiple threads instead of locking on each object.
    private static final Object lock = new Object();

    // This is the data we want to keep in sync with other replicas.
    private final List<Integer> valueTimestamp;
    private final List<Observation> observations;
    private final Map<String, Camera> cameras;

    // The update information!
    private final List<Integer> replicaTimestamp;
    private final List<ReplicaLog> log;
    private final List<String> operationsTable;

    private final List<List<Integer>> tableTimestamps;

    // The update thread that runs every X time.
    private final Thread updateThread;

    public ReplicaManager(Options options) {
        LOGGER.info("Replica manager created with options: " + options.toString());

        this.options = options;

        this.tableTimestamps = new ArrayList<>();

        List<Integer> cleanList = new ArrayList<>();

        for (int i = 0; i < options.getTotalInstances(); i++) {
            cleanList.add(0);
        }

        for (int i = 0; i < options.getTotalInstances(); i++) {
            this.tableTimestamps.add(new ArrayList<>(cleanList));
        }

        this.updateThread = update(options);
        this.updateThread.start();

        Store store = null;
        if (!options.getStorageFile().equals("ignore")) {
            try {
                String filePath = options.getStorageFile();
                File f = new File(filePath);
                if (f.exists() && !f.isDirectory()) {
                    FileInputStream fis = new FileInputStream(filePath);
                    ObjectInputStream in = new ObjectInputStream(fis);
                    store = (Store) in.readObject();
                }
            } catch (Exception ex) {
                LOGGER.info("Could not read store: " + options.getStorageFile());
            }
        }

        if (store == null) {
            valueTimestamp = Collections.synchronizedList(new ArrayList<>(cleanList));
            observations = Collections.synchronizedList(new ArrayList<>());
            cameras = Collections.synchronizedMap(new HashMap<>());

            // The update information!
            replicaTimestamp = Collections.synchronizedList(new ArrayList<>(cleanList));
            log = Collections.synchronizedList(new ArrayList<>());
            operationsTable = Collections.synchronizedList(new ArrayList<>());
        } else {
            valueTimestamp = Collections.synchronizedList(new ArrayList<>(store.getValueTimestamp()));
            observations = Collections.synchronizedList(new ArrayList<>(store.getObservations()));
            cameras = Collections.synchronizedMap(new HashMap<>(store.getCameras()));

            // The update information!
            replicaTimestamp = Collections.synchronizedList(new ArrayList<>(store.getReplicaTimestamp()));
            log = Collections.synchronizedList(new ArrayList<>(store.getLog()));
            operationsTable = Collections.synchronizedList(new ArrayList<>(store.getOperationsTable()));

            LOGGER.info("Information loaded from store: " + options.getStorageFile());
        }

        this.tableTimestamps.set(options.getInstance(), this.replicaTimestamp);
    }

    private void store () {
        if (options.getStorageFile().equals("ignore")) {
            return;
        }

        try {
            File file = new File(options.getStorageFile());
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(new Store(
                valueTimestamp,
                observations,
                cameras,
                replicaTimestamp,
                log,
                operationsTable
            ));
            out.close();
        } catch (Exception e) {
            LOGGER.info("Could not store file: " + e.toString());
        }
    }

    private Thread update (Options options) {
        return new Thread(() -> {
            List<Replica> replicas = new ArrayList<>();

            for (int i = 0; i < options.getTotalInstances(); i++) {
                if (i == options.getInstance()) {
                    continue;
                }
                replicas.add(new Replica(options.getInstance(), i, options.getInstanceTarget(i)));
            }

            while (true)  {
                LOGGER.info("UPDATE THREAD");

                for (Replica replica : replicas) {
                    List<Integer> thisTS = this.tableTimestamps.get(replica.getInstance());
                    List<ReplicaLog> toSend = new ArrayList<>();

                    for (ReplicaLog r : this.log) {
                        if (thisTS.get(r.getInstance()) < r.getTimestamp().get(r.getInstance())) {
                            toSend.add(r);
                        }
                    }

                    replica.gossip(toSend, this.replicaTimestamp);
                }

                try {
                    Thread.sleep(options.getUpdateFrequency());
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
                for (int i = 0; i < options.getTotalInstances(); i++) {
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

            for (int i = 0; i < options.getTotalInstances(); i++) {
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

        for (ReplicaLog r : this.log) {
            int c = r.getInstance();

            boolean canRemove = true;

            for (int i = 0; i < options.getTotalInstances(); i++) {
                if (tableTimestamps.get(i).get(c) < r.getTimestamp().get(c)) {
                    canRemove = false;
                    break;
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
        if (prev == null || prev.size() != options.getTotalInstances()) {
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

            for (int i = 0; i < options.getTotalInstances(); i++) {
                if (r.getTimestamp().get(i) > valueTimestamp.get(i)) {
                    valueTimestamp.set(i, r.getTimestamp().get(i));
                }
            }

            LOGGER.info("Executed (add) " + r.getUuid());
            store();
        }
    }

    private void executeAux (ReplicaLog r) {
        if (r.getCamera() != null) {
            this.cameras.put(r.getCamera().getName(), r.getCamera());
            LOGGER.info("Camera added to the stable value: " + r.getCamera().toString());
        } else if (r.getObservations() != null) {
            if (r.getObservations().isEmpty()) {
                LOGGER.info("Observations list is empty, skipping.");
                return;
            }

            String cameraName = r.getObservations().get(0).getCameraName();
            Camera camera = cameras.getOrDefault(cameraName, null);

            if (camera == null) {
                // NOTE: this must not happen. If it does, there is some _big_ error on the code.
                // At this point, the cameras map must be correctly synchronized.
                LOGGER.severe("Camera " + cameraName + "does not exist.");
                return;
            }

            for (Observation observation : r.getObservations()) {
                this.observations.add(observation);
                LOGGER.info("Observation added to the stable value: " + observation.toString());
            }
        }
    }

    private ReplicaResponse add (List<Integer> prev, String uuid, Camera camera, List<Observation> observations) {
        if (prev == null || prev.size() != options.getTotalInstances()) {
            prev = new ArrayList<>();
            for (int i = 0; i < options.getTotalInstances(); i++) prev.add(0);
        }


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

            this.replicaTimestamp.set(options.getInstance(), this.replicaTimestamp.get(options.getInstance()) + 1);
            newTimestamp.set(options.getInstance(), this.replicaTimestamp.get(options.getInstance()));

            ReplicaLog r = null;

            if (camera != null) {
                LOGGER.info("Adding camera to log: " + uuid);
                r = new ReplicaLog(options.getInstance(), prev, newTimestamp, uuid, camera);
                log.add(r);
            } else if (observations != null) {
                LOGGER.info("Adding observations to log " + uuid);
                r = new ReplicaLog(options.getInstance(), prev, newTimestamp, uuid, observations);
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
            for (int i = 0; i < options.getTotalInstances(); i++) {
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

    public void close () {
        synchronized (lock) {
            updateThread.interrupt();
            try {
                updateThread.join();
            } catch (InterruptedException ignored) {}
        }
    }
}
package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.InvalidCameraCoordinatesException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraNameException;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Sauron {
    private final Options options;
    private ReplicaManager replicaManager;

    // TODO(add to report): control operations are not distributed since
    //  they're only used for testing purposes https://piazza.com/class/k6cbgwcjrk11og?cid=194

    public Sauron (Options options) {
        this.options = options;
        replicaManager = new ReplicaManager(options);
    }

    public ReplicaResponse ctrlPing(List<Integer> prev) {
        return replicaManager.getAll(prev);
    }

    public void ctrlClear() {
        replicaManager.close();
        replicaManager = new ReplicaManager(options);
    }

    public void ctrlInit() {
        // EMPTY
    }

    public ReplicaResponse addCamera(List<Integer> prev, String uuid, String name, Double latitude, Double longitude)
        throws InvalidCameraNameException, InvalidCameraCoordinatesException {

        Camera camera = getCamera(prev, name).getCamera();
        if (camera != null && (!camera.getLatitude().equals(latitude) || !camera.getLongitude().equals(longitude))) {
            // In this case, we have a duplicate camera, i.e., a camera with the same name and different coordinates.
            // In the worst case scenario, we add a duplicate camera to the replica, but we can check that on the
            // ReplicaManager.
            return null;
        }

        if (camera != null) {
            // The camera already exists, but is not duplicated.
            return new ReplicaResponse(prev);
        }

        camera = new Camera(name, latitude, longitude);
        return replicaManager.addCamera(prev, uuid, camera);
    }

    public ReplicaResponse getCamera(List<Integer> prev, String name) {
        ReplicaResponse req = replicaManager.getCameras(prev);
        ReplicaResponse res = new ReplicaResponse(req.getTimestamp());
        res.setCamera(req.getCameras().getOrDefault(name, null));
        return res;
    }

    public ReplicaResponse track(List<Integer> prev, ObservationType type, String identifier) {
        ReplicaResponse req = replicaManager.getObservations(prev);
        ReplicaResponse res = new ReplicaResponse(req.getTimestamp());

        Observation lastObservation = req.getObservations().stream()
            .filter(observation -> observation.getType().equals(type) && identifier.equals(observation.getIdentifier()))
            .max(Comparator.comparing(Observation::getDatetime))
            .orElse(null);

        res.setObservation(lastObservation);
        return res;
    }

    public ReplicaResponse trackMatch(List<Integer> prev, ObservationType type, String pattern) {
        ReplicaResponse req = replicaManager.getObservations(prev);
        ReplicaResponse res = new ReplicaResponse(req.getTimestamp());

        String regex = buildRegex(type, pattern);

        Collection<List<Observation>> matches = req.getObservations().stream()
            .filter(observation -> observation.getType().equals(type) && observation.getIdentifier().matches(regex))
            .collect(Collectors.groupingBy(Observation::getIdentifier))
            .values();

        List<Observation> firstMatches = matches.stream()
            .map(element -> element.stream()
                .max(Comparator.comparing(Observation::getDatetime))
                .orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        res.setObservations(firstMatches);
        return res;
    }

    public ReplicaResponse trace(List<Integer> prev, ObservationType type, String identifier) {
        ReplicaResponse req = replicaManager.getObservations(prev);
        ReplicaResponse res = new ReplicaResponse(req.getTimestamp());

        List<Observation> observationsMatch = req.getObservations().stream()
            .filter(observation -> observation.getType().equals(type) && identifier.equals(observation.getIdentifier()))
            .sorted(Comparator.comparing(Observation::getDatetime).reversed())
            .collect(Collectors.toList());

        res.setObservations(observationsMatch);
        return res;
    }


    // TODO(add to report): we removed the InvalidCameraException because we can't guarantee this
    //  replica is updated in the moment we make the request. https://piazza.com/class/k6cbgwcjrk11og?cid=181
    public ReplicaResponse report (List<Integer> prev, String uuid, List<Observation> observations) {
        return replicaManager.addObservations(prev, uuid, observations);
    }

    public void receiveGossip (List<Integer> sourceTimestamp, Integer sourceInstance, List<ReplicaLog> log) {
        replicaManager.receiveGossip(sourceTimestamp, sourceInstance, log);
    }

    private String buildRegex(ObservationType type, String pattern) {
        String patternRegex = "^".concat(pattern).concat("$");

        switch (type) {
            case PERSON:
                return patternRegex.replace("*", "\\d*");
            case CAR:
                return patternRegex.replace("*", "\\w*");
            default:
                return patternRegex;
        }
    }
}
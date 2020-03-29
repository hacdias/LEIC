package pt.tecnico.sauron.silo.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import pt.tecnico.sauron.silo.exceptions.InvalidIdentifierException;
import pt.tecnico.sauron.silo.exceptions.DuplicateCameraException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraException;

public class Sauron {
    private List<Observation> observations = new ArrayList<Observation>();
    private List<Camera> cameras = new ArrayList<Camera>();

    public void ctrlPing() {
        // TODO
    }

    public void ctrlClear() {
        // TODO
    }

    public void ctrlInit() {
        // TODO
    }

    public void addCamera(String name, Float latitude, Float longitude) throws DuplicateCameraException {
        try {
            getCamera(name);
            throw new DuplicateCameraException(name);
        } catch (InvalidCameraException e) {
            Coordinates coordinates = new Coordinates(latitude, longitude);
            Camera camera = new Camera(name, coordinates);
            cameras.add(camera);
        }
    }

    public Camera getCamera(String name) throws InvalidCameraException {
        for (Camera cam : cameras) {
            if (cam.getName().equals(name)) {
                return cam;
            }
        }

        throw new InvalidCameraException(name);
    }

    public Observation track(ObservationType type, String identifier) {
        // TODO
        return null;
    }

    public Observation trackMatch(ObservationType type, String pattern) {
        // TODO
        return null;
    }

    public List<Observation> trace(ObservationType type, String identifier) {
        // TODO
        return null;
    }

    public void report (String name, ObservationType type, String identifier) throws InvalidCameraException, InvalidIdentifierException {
        Camera camera = getCamera(name);
        Observation observation = new Observation(camera, type, identifier, LocalDateTime.now());
        observations.add(observation);
    }
}
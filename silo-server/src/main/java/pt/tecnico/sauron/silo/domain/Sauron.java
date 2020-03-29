package pt.tecnico.sauron.silo.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import pt.tecnico.sauron.silo.exceptions.InvalidIdentifierException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraException;

public class Sauron {
    private List<Observation> observations = new ArrayList<Observation>();
    private List<Camera> cameras = new ArrayList<Camera>();

    public void addCamera (String name, Float latitude, Float longitude) {
        try {
            getCamera(name);
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

    /* public Observation track(ObservationObject type, Identifier identifier) {
        return null;
    } */

    /* public Observation trackMatch(ObservationObject type, String partIdentifier) {
        return null;
    } */

    /* public List<Observation> trace(ObservationObject type, Identifier identifier) {
        return null;
    } */

    public void report (String name, ObservationType type, String identifier) throws InvalidCameraException, InvalidIdentifierException {
        Camera camera = getCamera(name);
        Observation observation = new Observation(camera, type, identifier, LocalDateTime.now());
        observations.add(observation);
    }
}
package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.InvalidCameraNameException;

public class Camera {
    private String name;
    private Coordinates coordinates;

    public Camera(String name, Coordinates coordinates) throws InvalidCameraNameException {
        this.name = name;
        this.coordinates = coordinates;

        if(!(name.matches("^(\\d|\\w)*$") && name.length() >= 3 && name.length() <= 15)){
            throw new InvalidCameraNameException(name);
        }
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return "Camera(" + name + ")@" + coordinates.toString();
    }
}

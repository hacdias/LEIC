package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.InvalidCameraCoordinatesException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraNameException;

import java.io.Serializable;
import java.util.Objects;

public class Camera implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final Double latitude;
    private final Double longitude;

    public Camera(String name, Double latitude, Double longitude) throws InvalidCameraNameException, InvalidCameraCoordinatesException {
        this.name = name;

        if(!(name.matches("^(\\d|\\w)*$") && name.length() >= 3 && name.length() <= 15)){
            throw new InvalidCameraNameException(name);
        }

        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new InvalidCameraCoordinatesException(latitude, longitude);
        }

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Camera camera = (Camera) o;
        return name.equals(camera.getName()) &&
            latitude.equals(camera.getLatitude()) &&
            longitude.equals(camera.getLongitude());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, latitude, longitude);
    }

    @Override
    public String toString() {
        return "Camera(" + name + ")@(" + latitude + "," + longitude + ")";
    }
}

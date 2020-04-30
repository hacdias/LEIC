package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.InvalidCameraCoordinatesException;

import java.util.Objects;

public class Coordinates {
    private Double latitude;
    private Double longitude;

    public Coordinates(Double latitude, Double longitude) throws InvalidCameraCoordinatesException {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new InvalidCameraCoordinatesException(latitude, longitude);
        }

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Coordinates(" + latitude + "," + longitude + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
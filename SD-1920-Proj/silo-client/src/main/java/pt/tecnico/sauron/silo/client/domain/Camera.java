package pt.tecnico.sauron.silo.client.domain;

import pt.tecnico.sauron.silo.grpc.Silo;

public class Camera {
    private final String name;
    private final Double latitude;
    private final Double longitude;

    public Camera(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Camera(Silo.Camera camera) {
        this.name = camera.getName();
        this.latitude = camera.getLatitude();
        this.longitude = camera.getLongitude();
    }

    public String getName() {
        return name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    @Override
    public String toString() {
        return "Camera(" + name + ")@" + "(" + latitude + "," + longitude + ")";
    }
}

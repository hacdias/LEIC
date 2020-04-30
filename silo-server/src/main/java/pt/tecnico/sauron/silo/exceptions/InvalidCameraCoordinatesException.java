package pt.tecnico.sauron.silo.exceptions;

public class InvalidCameraCoordinatesException extends Exception {
    private static final long serialVersionUID = 1L;
    private final Double latitude;
    private final Double longitude;

    public InvalidCameraCoordinatesException(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String toString(){
        return ("InvalidCameraCoordinatesException Occurred: " + latitude + ", " + longitude) ;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
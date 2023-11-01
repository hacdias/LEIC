package pt.tecnico.sauron.silo.client.domain;

import com.google.protobuf.Timestamp;
import pt.tecnico.sauron.silo.grpc.Silo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Map;

public class Observation {
    private ObservationType type;
    private String identifier;
    private LocalDateTime datetime;
    private Camera camera;

    private final static Map<Silo.ObservationType, ObservationType> typesConverter = Map.ofEntries(
        Map.entry(Silo.ObservationType.PERSON, ObservationType.PERSON),
        Map.entry(Silo.ObservationType.CAR, ObservationType.CAR));

    public Observation(Camera camera, ObservationType type, String identifier, LocalDateTime datetime) {
        this.type = type;
        this.identifier = identifier;
        this.datetime = datetime;
        this.camera = camera;
    }

    public Observation(Silo.Camera camera, Silo.Observation observation) {
        this.type = typesConverter.get(observation.getType());
        this.identifier = observation.getIdentifier();
        Timestamp ts = observation.getTimestamp();
        this.datetime = Instant
            .ofEpochSecond(ts.getSeconds() , ts.getNanos())
            .atZone(ZoneOffset.UTC)
            .toLocalDateTime();
        this.camera = new Camera(camera);
    }

    public ObservationType getType() {
        return type;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public String toString() {
        return "Observation(" + type + "," +identifier + ")@" + datetime.toString() + " by " + camera.toString();
    }

    public static class IdentifierComparator implements Comparator<Observation> {
        public int compare(Observation a, Observation b) {
            return a.getIdentifier().compareTo(b.getIdentifier());
        }
    }
}
package pt.tecnico.sauron.silo.domain;

import com.google.protobuf.Timestamp;
import pt.tecnico.sauron.silo.grpc.Silo;

import java.time.ZoneOffset;
import java.util.Map;

public class Converter {
    private static final Map<ObservationType, Silo.ObservationType> inverseTypeConverter = Map.ofEntries(
        Map.entry(ObservationType.PERSON, Silo.ObservationType.PERSON),
        Map.entry(ObservationType.CAR, Silo.ObservationType.CAR)
    );

    public static Silo.ObservationInfo convertToMessage(Observation observation) {
        return Silo.ObservationInfo.newBuilder()
            .setCamera(Silo.Camera.newBuilder()
                .setName(observation.getCamera().getName())
                .setCoordinates(Silo.Coordinates.newBuilder()
                    .setLatitude(observation.getCamera().getCoordinates().getLatitude())
                    .setLongitude(observation.getCamera().getCoordinates().getLongitude())
                    .build())
                .build())
            .setObservation(Silo.Observation.newBuilder()
                .setIdentifier(observation.getIdentifier())
                .setTimestamp(Timestamp.newBuilder()
                    .setSeconds(observation.getDatetime().toInstant(ZoneOffset.UTC).getEpochSecond())
                    .setNanos(observation.getDatetime().toInstant(ZoneOffset.UTC).getNano())
                    .build())
                .setType(inverseTypeConverter.get(observation.getType())))
            .build();
    }

    public static Silo.Camera convertToMessage(Camera camera) {
        return Silo.Camera.newBuilder()
            .setName(camera.getName())
            .setCoordinates(convertToMessage(camera.getCoordinates()))
            .build();
    }

    public static Silo.Coordinates convertToMessage(Coordinates coordinates) {
        return Silo.Coordinates.newBuilder()
            .setLatitude(coordinates.getLatitude())
            .setLongitude(coordinates.getLongitude())
            .build();
    }
}
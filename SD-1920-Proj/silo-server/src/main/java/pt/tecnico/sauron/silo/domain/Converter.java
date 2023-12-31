package pt.tecnico.sauron.silo.domain;

import com.google.protobuf.Timestamp;
import pt.tecnico.sauron.silo.grpc.Silo;

import java.time.ZoneOffset;
import java.util.Map;

public class Converter {
    public static Silo.ObservationInfo convertToMessage(Observation observation, Map<String,Camera> cameras) {
        return Silo.ObservationInfo.newBuilder()
            .setCamera(Silo.Camera.newBuilder()
                .setName(observation.getCameraName())
                .setLatitude(cameras.get(observation.getCameraName()).getLatitude())
                .setLongitude(cameras.get(observation.getCameraName()).getLongitude())
                .build())
            .setObservation(Silo.Observation.newBuilder()
                .setIdentifier(observation.getIdentifier())
                .setTimestamp(Timestamp.newBuilder()
                    .setSeconds(observation.getDatetime().toInstant(ZoneOffset.UTC).getEpochSecond())
                    .setNanos(observation.getDatetime().toInstant(ZoneOffset.UTC).getNano())
                    .build())
                .setType(ObservationType.toSiloType.get(observation.getType())))
            .build();
    }

    public static Silo.Camera convertToMessage(Camera camera) {
        return Silo.Camera.newBuilder()
            .setName(camera.getName())
            .setLatitude(camera.getLatitude())
            .setLongitude(camera.getLongitude())
            .build();
    }
}
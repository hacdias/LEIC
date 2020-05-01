package pt.tecnico.sauron.silo.domain;

import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.grpc.SauronGrpc;
import pt.tecnico.sauron.silo.grpc.Silo;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Replica implements Closeable {
    private final Integer instance;
    private final Integer currentInstance;
    private final String target;

    private SauronGrpc.SauronBlockingStub stub;
    private ManagedChannel channel;

    private static final Logger LOGGER = Logger.getLogger(Replica.class.getName());

    private final Map<ObservationType, Silo.ObservationType> typesConverter = Map.ofEntries(
        Map.entry(ObservationType.PERSON, Silo.ObservationType.PERSON),
        Map.entry(ObservationType.CAR, Silo.ObservationType.CAR)
    );

    public Replica (Integer currentInstance, Integer instance, String target) {
        this.currentInstance = currentInstance;
        this.instance = instance;
        this.target = target;

        channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        stub = SauronGrpc.newBlockingStub(channel);
    }

    public void gossip (List<ReplicaLog> log, List<Integer> replicaTimestamp) {
        LOGGER.info("Gossipping to " + instance.toString());

        // TODO: filter

        List<Silo.Log> siloLogs = new ArrayList<>();

        for (ReplicaLog r : log) {
            if (r.getCamera() != null) {
                siloLogs.add(getCameraLog(r));
            } else if (r.getObservations() != null && !r.getObservations().isEmpty()) {
                siloLogs.add(getObservationsLog(r));
            }
        }

        Silo.GossipRequest gossip = Silo.GossipRequest.newBuilder()
            .setTimestamp(Silo.Timestamp.newBuilder().addAllValue(replicaTimestamp))
            .addAllLog(siloLogs)
            .setInstance(this.currentInstance)
            .build();

        try {
            stub.gossip(gossip);
            LOGGER.info("Gossiped to instance " + instance.toString());
        } catch (io.grpc.StatusRuntimeException e) {
            channel.shutdown();

            // Recreate channel and stub.
            channel = ManagedChannelBuilder.forTarget(this.target).usePlaintext().build();
            stub = SauronGrpc.newBlockingStub(channel);

            LOGGER.info("Gossip error on instance " + instance.toString() + ": " + e.toString());
        }
    }

    private Silo.Log getCameraLog (ReplicaLog log) {
        Camera burraNasCouves = log.getCamera();

        Silo.Camera camera = Silo.Camera.newBuilder()
            .setName(burraNasCouves.getName())
            .setLatitude(burraNasCouves.getLatitude())
            .setLongitude(burraNasCouves.getLongitude())
            .build();

        return Silo.Log.newBuilder()
            .setInstance(log.getInstance())
            .setUuid(log.getUuid())
            .setCamera(camera)
            .setTimestamp(Silo.Timestamp.newBuilder().addAllValue(log.getTimestamp()).build())
            .setPrev(Silo.Timestamp.newBuilder().addAllValue(log.getPrev()).build())
            .build();
    }

    private Silo.Log getObservationsLog (ReplicaLog log) {
        List<Observation> burraNasCouves = log.getObservations();

        List<Silo.Observation> observations = burraNasCouves
            .stream()
            .map(observation -> {
                Instant instant = observation.getDatetime().atZone(ZoneOffset.UTC).toInstant();

                return Silo.Observation
                    .newBuilder()
                    .setCameraName(observation.getCameraName())
                    .setIdentifier(observation.getIdentifier())
                    .setType(typesConverter.get(observation.getType()))
                    .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(instant.getEpochSecond())
                        .setNanos(instant.getNano())
                        .build()
                    )
                    .build();
            })
            .collect(Collectors.toList());

        return Silo.Log.newBuilder()
            .setInstance(log.getInstance())
            .setUuid(log.getUuid())
            .addAllObservations(observations)
            .setTimestamp(Silo.Timestamp.newBuilder().addAllValue(log.getTimestamp()).build())
            .setPrev(Silo.Timestamp.newBuilder().addAllValue(log.getPrev()).build())
            .build();
    }

    @Override
    public void close() throws IOException {
        channel.shutdown();
    }
}

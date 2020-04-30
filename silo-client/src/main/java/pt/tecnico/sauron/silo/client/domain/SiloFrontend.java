package pt.tecnico.sauron.silo.client.domain;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.protobuf.Timestamp;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.client.exceptions.DuplicateCameraException;
import pt.tecnico.sauron.silo.client.exceptions.InvalidCameraCoordinatesException;
import pt.tecnico.sauron.silo.client.exceptions.InvalidCameraException;
import pt.tecnico.sauron.silo.client.exceptions.InvalidCameraNameException;
import pt.tecnico.sauron.silo.client.exceptions.InvalidIdentifierException;
import pt.tecnico.sauron.silo.client.exceptions.NoObservationFoundException;
import pt.tecnico.sauron.silo.client.exceptions.SauronClientException;
import pt.tecnico.sauron.silo.client.exceptions.UnknownException;
import pt.tecnico.sauron.silo.grpc.SauronGrpc;
import pt.tecnico.sauron.silo.grpc.Silo;
import pt.tecnico.sauron.silo.grpc.Silo.CamInfoRequest;
import pt.tecnico.sauron.silo.grpc.Silo.CamInfoResponse;
import pt.tecnico.sauron.silo.grpc.Silo.CamJoinRequest;
import pt.tecnico.sauron.silo.grpc.Silo.CamJoinResponse;
import pt.tecnico.sauron.silo.grpc.Silo.CtrlClearRequest;
import pt.tecnico.sauron.silo.grpc.Silo.CtrlClearResponse;
import pt.tecnico.sauron.silo.grpc.Silo.CtrlPingRequest;
import pt.tecnico.sauron.silo.grpc.Silo.CtrlPingResponse;
import pt.tecnico.sauron.silo.grpc.Silo.ReportRequest;
import pt.tecnico.sauron.silo.grpc.Silo.ReportResponse;
import pt.tecnico.sauron.silo.grpc.Silo.TraceRequest;
import pt.tecnico.sauron.silo.grpc.Silo.TraceResponse;
import pt.tecnico.sauron.silo.grpc.Silo.TrackMatchRequest;
import pt.tecnico.sauron.silo.grpc.Silo.TrackMatchResponse;
import pt.tecnico.sauron.silo.grpc.Silo.TrackRequest;
import pt.tecnico.sauron.silo.grpc.Silo.TrackResponse;

import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

public class SiloFrontend implements AutoCloseable {
    private List<Integer> timestamp = new ArrayList<>();

    private final ZKNaming zkNaming;
    private final String basePath = "/grpc/sauron/silo";

    private ManagedChannel channel;
    private SauronGrpc.SauronBlockingStub stub;

    private static final Logger LOGGER = Logger.getLogger(SiloFrontend.class.getName());

    private final Map<ObservationType, Silo.ObservationType> typesConverter = Map.ofEntries(
            Map.entry(ObservationType.PERSON, Silo.ObservationType.PERSON),
            Map.entry(ObservationType.CAR, Silo.ObservationType.CAR)
    );

    public SiloFrontend(String address, Integer port, Integer instance) {
        this.zkNaming = new ZKNaming(address, Integer.toString(port));
        connectToReplica(instance);
    }

    private void connectToReplica(Integer instance) {
        String path = basePath;
        ZKRecord record = null;

        try {
            if (instance != -1) {
                LOGGER.info("Connecting to instance " + instance + " of server ");
                path += "/" + Integer.toString(instance);
                record = zkNaming.lookup(path);
            } else {
                LOGGER.info("Connecting to a random instance.");
                Collection<ZKRecord> available = zkNaming.listRecords(path);
                // FIXME
                int num = new Random().nextInt(available.size());
                for (ZKRecord r : available) {
                    if (--num < 0) {
                        record = r;
                    }
                }

                if (record == null) {
                    throw new ZKNamingException("No record available");
                }
            }

            LOGGER.info("Connected to: " + record);
            String target = record.getURI();

            channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            stub = SauronGrpc.newBlockingStub(channel);
        } catch (ZKNamingException e) {
            // TODO
            LOGGER.severe(e.toString());
        }
    }

    private Silo.Timestamp buildTimestamp() {
        return Silo.Timestamp.newBuilder().addAllValue(timestamp).build();
    }

    private void updateTimestamp(List<Integer> timestamp) {
        if (this.timestamp.isEmpty()) {
            this.timestamp = new ArrayList<>(timestamp);
            return;
        }

        if (timestamp.isEmpty()) {
            this.timestamp = new ArrayList<>();
            return;
        }

        for (int i = 0; i < timestamp.size(); i++) {
            if (timestamp.get(i) > this.timestamp.get(i)) {
                this.timestamp.set(i, timestamp.get(i));
            }
        }
    }

    public Status ping() throws SauronClientException {
        CtrlPingRequest request = CtrlPingRequest.newBuilder().setTimestamp(buildTimestamp()).build();
        CtrlPingResponse response = stub.ctrlPing(request);

        updateTimestamp(response.getTimestamp().getValueList());

        throwIfNotSuccess(response.getStatus());

        List<Camera> cameras = response.getCamerasList()
                .stream()
                .map(camera -> new Camera(camera))
                .collect(Collectors.toList());

        List<Observation> observations = response.getObservationsList()
                .stream()
                .map(observation -> new Observation(observation.getCamera(), observation.getObservation()))
                .collect(Collectors.toList());

        return new Status(cameras, observations);
    }

    public String init() {
        // TODO
        return null;
    }

    public void clear() throws SauronClientException {
        CtrlClearRequest request = CtrlClearRequest.newBuilder().setTimestamp(buildTimestamp()).build();
        CtrlClearResponse response = stub.ctrlClear(request);
        updateTimestamp(response.getTimestamp().getValueList());
        throwIfNotSuccess(response.getStatus());
    }

    public void camJoin(String name, Double latitude, Double longitude) throws SauronClientException {
        Silo.Coordinates coordinates = Silo.Coordinates.newBuilder()
                .setLatitude(latitude)
                .setLongitude(longitude)
                .build();

        Silo.Camera camera = Silo.Camera.newBuilder()
                .setName(name)
                .setCoordinates(coordinates)
                .build();

        CamJoinRequest request = CamJoinRequest.newBuilder()
                .setTimestamp(buildTimestamp())
                .setCamera(camera)
                .build();

        CamJoinResponse response = stub.camJoin(request);
        updateTimestamp(response.getTimestamp().getValueList());
        throwIfNotSuccess(response.getStatus());
    }

    public Coordinates camInfo(String name) throws SauronClientException {
        CamInfoRequest request = CamInfoRequest.newBuilder()
                .setTimestamp(buildTimestamp())
                .setName(name)
                .build();

        CamInfoResponse response = stub.camInfo(request);
        updateTimestamp(response.getTimestamp().getValueList());
        throwIfNotSuccess(response.getStatus());
        return new Coordinates(response.getCoordinates());
    }

    public void report(String camName, List<Observation> observations) throws SauronClientException {
        List<Silo.Observation> siloObservations = observations
                .stream()
                .map(observation -> {
                    Instant instant = observation.getDatetime().atZone(ZoneOffset.UTC).toInstant();

                    return Silo.Observation
                            .newBuilder()
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

        ReportRequest request = ReportRequest.newBuilder()
                .setTimestamp(buildTimestamp())
                .setCameraName(camName)
                .addAllObservations(siloObservations)
                .build();

        ReportResponse response = stub.report(request);
        updateTimestamp(response.getTimestamp().getValueList());
        throwIfNotSuccess(response.getStatus());
    }

    public Observation track(ObservationType type, String identifier) throws SauronClientException {
        TrackRequest request = TrackRequest.newBuilder()
                .setTimestamp(buildTimestamp())
                .setType(typesConverter.get(type))
                .setIdentifier(identifier)
                .build();

        TrackResponse response = stub.track(request);
        updateTimestamp(response.getTimestamp().getValueList());
        throwIfNotSuccess(response.getStatus());
        return new Observation(response.getObservation().getCamera(), response.getObservation().getObservation());
    }

    public List<Observation> trackMatch(ObservationType type, String pattern) throws SauronClientException {
        TrackMatchRequest request = TrackMatchRequest.newBuilder()
                .setTimestamp(buildTimestamp())
                .setType(typesConverter.get(type))
                .setPattern(pattern)
                .build();

        TrackMatchResponse response = stub.trackMatch(request);
        updateTimestamp(response.getTimestamp().getValueList());
        throwIfNotSuccess(response.getStatus());

        return response.getObservationsList()
                .stream()
                .map(info -> new Observation(info.getCamera(), info.getObservation()))
                .collect(Collectors.toList());
    }

    public List<Observation> trace(ObservationType type, String identifier) throws SauronClientException {
        TraceRequest request = TraceRequest.newBuilder()
                .setTimestamp(buildTimestamp())
                .setType(typesConverter.get(type))
                .setIdentifier(identifier)
                .build();

        TraceResponse response = stub.trace(request);
        updateTimestamp(response.getTimestamp().getValueList());
        throwIfNotSuccess(response.getStatus());

        return response.getObservationsList()
                .stream()
                .map(info -> new Observation(info.getCamera(), info.getObservation()))
                .collect(Collectors.toList());
    }

    private void throwIfNotSuccess(Silo.ResponseStatus status) throws SauronClientException {
        switch (status) {
            case SUCCESS:
                return;
            case DUPLICATE_CAMERA:
                throw new DuplicateCameraException();
            case INVALID_CAMERA:
                throw new InvalidCameraException();
            case INVALID_CAMERA_NAME:
                throw new InvalidCameraNameException();
            case INVALID_IDENTIFIER:
                throw new InvalidIdentifierException();
            case INVALID_CAMERA_COORDINATES:
                throw new InvalidCameraCoordinatesException();
            case NO_OBSERVATION_FOUND:
                throw new NoObservationFoundException();
            default:
                throw new UnknownException();
        }
    }

    @Override
    public final void close() {
        channel.shutdown();
    }
}
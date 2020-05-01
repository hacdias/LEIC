package pt.tecnico.sauron.silo.client.domain;

import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.client.exceptions.*;
import pt.tecnico.sauron.silo.grpc.SauronGrpc;
import pt.tecnico.sauron.silo.grpc.Silo;
import pt.tecnico.sauron.silo.grpc.Silo.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import javax.sound.midi.Track;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SiloFrontend implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(SiloFrontend.class.getName());
    private final ZKNaming zkNaming;
    private final String basePath = "/grpc/sauron/silo";

    private List<Integer> timestamp = new ArrayList<>();
    private ManagedChannel channel;
    private SauronGrpc.SauronBlockingStub stub;

    private Camera ourselves = null;

    private final Integer cacheSize;
    private final Queue<Request> queue = new LinkedList<>();
    private final Map<Request,Object> cache = new HashMap<>();

    public SiloFrontend(String address, Integer port, Integer instance, Integer cacheSize) throws ZKNamingException, SauronClientException {
        this.zkNaming = new ZKNaming(address, Integer.toString(port));
        this.cacheSize = cacheSize;
        connectToReplica(instance);
    }

    private void putInCache (Request req, Object data) {
        if (cacheSize == 0) return;

        if (queue.size() >= cacheSize) {
            LOGGER.info("Removed old cache element.");
            cache.remove(queue.remove());
        }

        LOGGER.info("Added req to cache.");

        if (!cache.containsKey(req)) {
            queue.add(req);
        }

        cache.put(req, data);
    }

    private Object getObjectOrDefault (Request req, List<Integer> timestamp, Object response) {
        if (cache.containsKey(req) && isTimestampOlder(timestamp)) {
            return cache.get(req);
        } else {
            putInCache(req, response);
            return response;
        }
    }

    private void connectToReplica(Integer instance) throws ZKNamingException, SauronClientException {
        String path = basePath;
        ZKRecord record = null;

        if (instance != -1) {
            path += "/" + Integer.toString(instance);
            record = zkNaming.lookup(path);
        } else {
            Collection<ZKRecord> available = zkNaming.listRecords(path);
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

        String target = record.getURI();
        channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        stub = SauronGrpc.newBlockingStub(channel);

        if (ourselves != null) {
            camJoin(ourselves.getName(), ourselves.getLatitude(), ourselves.getLongitude());
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

    private boolean isTimestampOlder (List<Integer> timestamp) {
        for (int i = 0; i < timestamp.size(); i++) {
            if (timestamp.get(i) < this.timestamp.get(i)) {
                return true;
            }
        }

        return false;
    }

    public Status ping() throws SauronClientException, ZKNamingException {
        CtrlPingRequest request = CtrlPingRequest.newBuilder().setTimestamp(buildTimestamp()).build();
        CtrlPingResponse response;

        try {
            response = stub.ctrlPing(request);
        } catch (io.grpc.StatusRuntimeException e) {
            connectToReplica(-1);
            return ping();
        }

        updateTimestamp(response.getTimestamp().getValueList());

        throwIfNotSuccess(response.getStatus());

        List<Camera> cameras = response.getCamerasList()
            .stream()
            .map(Camera::new)
            .collect(Collectors.toList());

        List<Observation> observations = response.getObservationsList()
            .stream()
            .map(observation -> new Observation(observation.getCamera(), observation.getObservation()))
            .collect(Collectors.toList());

        return new Status(cameras, observations);
    }

    public String init() {
        // EMPTY
        return null;
    }

    public void clear() throws SauronClientException, ZKNamingException {
        CtrlClearRequest request = CtrlClearRequest.newBuilder().setTimestamp(buildTimestamp()).build();
        CtrlClearResponse response;

        try {
            response = stub.ctrlClear(request);
        } catch (io.grpc.StatusRuntimeException e) {
            connectToReplica(-1);
            clear();
            return;
        }

        updateTimestamp(response.getTimestamp().getValueList());
        throwIfNotSuccess(response.getStatus());
    }

    public void camJoin(String name, Double latitude, Double longitude) throws SauronClientException, ZKNamingException {
        Silo.Camera camera = Silo.Camera.newBuilder()
            .setName(name)
            .setLatitude(latitude)
            .setLongitude(longitude)
            .build();

        CamJoinRequest request = CamJoinRequest.newBuilder()
            .setTimestamp(buildTimestamp())
            .setCamera(camera)
            .setUuid(UUID.randomUUID().toString())
            .build();

        CamJoinResponse response;

        try {
            response = stub.camJoin(request);
        } catch (io.grpc.StatusRuntimeException e) {
            connectToReplica(-1);
            camJoin(name, latitude, longitude);
            return;
        }

        updateTimestamp(response.getTimestamp().getValueList());
        throwIfNotSuccess(response.getStatus());
        ourselves = new Camera(name, latitude, longitude);
    }

    public Camera camInfo(String name) throws SauronClientException, ZKNamingException {
        CamInfoRequest request = CamInfoRequest.newBuilder()
            .setTimestamp(buildTimestamp())
            .setName(name)
            .build();

        Request r = new Request();
        r.setRequestType(CamInfoRequest.getDescriptor().getFullName());
        r.setCameraName(name);

        CamInfoResponse response;

        try {
            response = stub.camInfo(request);
        } catch (io.grpc.StatusRuntimeException e) {
            connectToReplica(-1);
            return camInfo(name);
        }

        response = (CamInfoResponse)getObjectOrDefault(r, response.getTimestamp().getValueList(), response);
        updateTimestamp(response.getTimestamp().getValueList());
        throwIfNotSuccess(response.getStatus());
        return new Camera(response.getCamera());
    }

    public void report(String camName, List<Observation> observations) throws SauronClientException, ZKNamingException {
        List<Silo.Observation> siloObservations = observations
            .stream()
            .map(observation -> {
                Instant instant = observation.getDatetime().atZone(ZoneOffset.UTC).toInstant();

                return Silo.Observation
                    .newBuilder()
                    .setIdentifier(observation.getIdentifier())
                    .setType(ObservationType.toSilo.get(observation.getType()))
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
            .setUuid(UUID.randomUUID().toString())
            .build();

        ReportResponse response;

        try {
            response = stub.report(request);
        } catch (io.grpc.StatusRuntimeException e) {
            connectToReplica(-1);
            report(camName, observations);
            return;
        }

        updateTimestamp(response.getTimestamp().getValueList());
        throwIfNotSuccess(response.getStatus());
    }

    public Observation track(ObservationType type, String identifier) throws SauronClientException, ZKNamingException {
        TrackRequest request = TrackRequest.newBuilder()
            .setTimestamp(buildTimestamp())
            .setType(ObservationType.toSilo.get(type))
            .setIdentifier(identifier)
            .build();

        Request r = new Request();
        r.setRequestType(TrackRequest.getDescriptor().getFullName());
        r.setObservationType(type);
        r.setIdentifier(identifier);

        TrackResponse response;
        try {
            response = stub.track(request);
        } catch (io.grpc.StatusRuntimeException e) {
            connectToReplica(-1);
            return track(type, identifier);
        }

        response = (TrackResponse)getObjectOrDefault(r, response.getTimestamp().getValueList(), response);

        updateTimestamp(response.getTimestamp().getValueList());
        throwIfNotSuccess(response.getStatus());
        return new Observation(response.getObservation().getCamera(), response.getObservation().getObservation());
    }

    public List<Observation> trackMatch(ObservationType type, String pattern) throws SauronClientException, ZKNamingException {
        TrackMatchRequest request = TrackMatchRequest.newBuilder()
            .setTimestamp(buildTimestamp())
            .setType(ObservationType.toSilo.get(type))
            .setPattern(pattern)
            .build();

        TrackMatchResponse response;

        Request r = new Request();
        r.setRequestType(TrackMatchRequest.getDescriptor().getFullName());
        r.setObservationType(type);
        r.setPattern(pattern);

        try {
            response = stub.trackMatch(request);
        } catch (io.grpc.StatusRuntimeException e) {
            connectToReplica(-1);
            return trackMatch(type, pattern);
        }

        response = (TrackMatchResponse)getObjectOrDefault(r, response.getTimestamp().getValueList(), response);

        updateTimestamp(response.getTimestamp().getValueList());
        throwIfNotSuccess(response.getStatus());

        return response.getObservationsList()
            .stream()
            .map(info -> new Observation(info.getCamera(), info.getObservation()))
            .collect(Collectors.toList());
    }

    public List<Observation> trace(ObservationType type, String identifier) throws SauronClientException, ZKNamingException {
        TraceRequest request = TraceRequest.newBuilder()
            .setTimestamp(buildTimestamp())
            .setType(ObservationType.toSilo.get(type))
            .setIdentifier(identifier)
            .build();

        Request r = new Request();
        r.setRequestType(TraceRequest.getDescriptor().getFullName());
        r.setObservationType(type);
        r.setIdentifier(identifier);

        TraceResponse response;

        try {
            response = stub.trace(request);
        } catch (io.grpc.StatusRuntimeException e) {
            connectToReplica(-1);
            return trace(type, identifier);
        }

        response = (TraceResponse)getObjectOrDefault(r, response.getTimestamp().getValueList(), response);

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

    private class Request {
        private String requestType = null;
        private ObservationType observationType = null;
        private String identifier = null;
        private String pattern = null;
        private String cameraName = null;

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public void setCameraName(String cameraName) {
            this.cameraName = cameraName;
        }

        public void setObservationType(ObservationType observationType) {
            this.observationType = observationType;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public void setRequestType(String requestType) {
            this.requestType = requestType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Request request = (Request) o;

            return Objects.equals(requestType, request.requestType) &&
                observationType == request.observationType &&
                Objects.equals(identifier, request.identifier) &&
                Objects.equals(pattern, request.pattern) &&
                Objects.equals(cameraName, request.cameraName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(requestType, observationType, identifier, pattern, cameraName);
        }
    }

    @Override
    public final void close() {
        channel.shutdown();
    }
}
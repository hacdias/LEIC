package pt.tecnico.sauron.silo;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.domain.*;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraCoordinatesException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraNameException;
import pt.tecnico.sauron.silo.exceptions.InvalidIdentifierException;
import pt.tecnico.sauron.silo.grpc.SauronGrpc;
import pt.tecnico.sauron.silo.grpc.Silo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SiloServiceImpl extends SauronGrpc.SauronImplBase {
    private Sauron sauron;

    public SiloServiceImpl(Options options) {
        super();
        this.sauron = new Sauron(options);
    }

    private Silo.Timestamp convertTimestamp (List<Integer> timestamp) {
        return Silo.Timestamp.newBuilder().addAllValue(timestamp).build();
    }

    @Override
    public void camJoin(Silo.CamJoinRequest request, StreamObserver<Silo.CamJoinResponse> responseObserver) {
        Silo.CamJoinResponse.Builder builder = Silo.CamJoinResponse.newBuilder();
        builder.setStatus(Silo.ResponseStatus.SUCCESS);
        List<Integer> prev = request.getTimestamp().getValueList();

        try {
            Silo.Camera cam = request.getCamera();
            Silo.Coordinates coordinates = cam.getCoordinates();
            ReplicaResponse res = sauron.addCamera(prev, request.getUuid(), cam.getName(), coordinates.getLatitude(),coordinates.getLongitude());

            if (res == null) {
                builder.setTimestamp(convertTimestamp(prev));
                builder.setStatus(Silo.ResponseStatus.DUPLICATE_CAMERA);
            } else {
                builder.setTimestamp(convertTimestamp(res.getTimestamp()));
            }
        } catch (InvalidCameraNameException e) {
            builder.setStatus(Silo.ResponseStatus.INVALID_CAMERA_NAME);
            builder.setTimestamp(convertTimestamp(prev));
        } catch (InvalidCameraCoordinatesException e) {
            builder.setStatus(Silo.ResponseStatus.INVALID_CAMERA_COORDINATES);
            builder.setTimestamp(convertTimestamp(prev));
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void camInfo(Silo.CamInfoRequest request, StreamObserver<Silo.CamInfoResponse> responseObserver) {
        Silo.CamInfoResponse.Builder builder = Silo.CamInfoResponse.newBuilder();
        List<Integer> prev = request.getTimestamp().getValueList();
        ReplicaResponse res = sauron.getCamera(prev, request.getName());

        if (res.getCamera() == null) {
            builder.setStatus(Silo.ResponseStatus.INVALID_CAMERA);
        } else {
            builder.setCoordinates(Converter.convertToMessage(res.getCamera().getCoordinates()));
        }

        builder.setTimestamp(convertTimestamp(res.getTimestamp()));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void track(Silo.TrackRequest request, StreamObserver<Silo.TrackResponse> responseObserver) {
        Silo.TrackResponse.Builder builder = Silo.TrackResponse.newBuilder();
        builder.setStatus(Silo.ResponseStatus.SUCCESS);
        List<Integer> prev = request.getTimestamp().getValueList();

        ObservationType type = ObservationType.fromSilo.get(request.getType());
        ReplicaResponse res = sauron.track(prev, type, request.getIdentifier());

        if (res.getObservation() == null) {
            builder.setStatus(Silo.ResponseStatus.NO_OBSERVATION_FOUND);
        } else {
            Silo.ObservationInfo observationInfo = Converter.convertToMessage(res.getObservation());
            builder.setObservation(observationInfo);
        }

        builder.setTimestamp(convertTimestamp(res.getTimestamp()));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void trackMatch(Silo.TrackMatchRequest request, StreamObserver<Silo.TrackMatchResponse> responseObserver) {
        Silo.TrackMatchResponse.Builder builder = Silo.TrackMatchResponse.newBuilder();
        builder.setStatus(Silo.ResponseStatus.SUCCESS);
        List<Integer> prev = request.getTimestamp().getValueList();

        ObservationType type = ObservationType.fromSilo.get(request.getType());
        ReplicaResponse res = sauron.trackMatch(prev, type, request.getPattern());

        if (res.getObservations().isEmpty()) {
            builder.setStatus(Silo.ResponseStatus.NO_OBSERVATION_FOUND);
        } else {
            List<Silo.ObservationInfo> observationsInfo = res.getObservations().stream()
                .map(Converter::convertToMessage)
                .collect(Collectors.toList());

            builder.addAllObservations(observationsInfo);
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void trace(Silo.TraceRequest request, StreamObserver<Silo.TraceResponse> responseObserver) {
        Silo.TraceResponse.Builder builder = Silo.TraceResponse.newBuilder();
        builder.setStatus(Silo.ResponseStatus.SUCCESS);
        List<Integer> prev = request.getTimestamp().getValueList();

        ObservationType type = ObservationType.fromSilo.get(request.getType());
        ReplicaResponse res = sauron.trace(prev, type, request.getIdentifier());

        if (res.getObservations().isEmpty()) {
            builder.setStatus(Silo.ResponseStatus.NO_OBSERVATION_FOUND);
        } else {
            List<Silo.ObservationInfo> observationInfos = res.getObservations().stream()
                .map(Converter::convertToMessage)
                .collect(Collectors.toList());

            builder.addAllObservations(observationInfos);
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void report(Silo.ReportRequest request, StreamObserver<Silo.ReportResponse> responseObserver) {
        Silo.ReportResponse.Builder builder = Silo.ReportResponse.newBuilder();
        List<Integer> prev = request.getTimestamp().getValueList();
        List<Observation> observations = new ArrayList<>();

        try {
            for (Silo.Observation observation : request.getObservationsList()) {
                ObservationType type = ObservationType.fromSilo.get(observation.getType());
                observations.add(new Observation(request.getCameraName(), type, observation.getIdentifier(), LocalDateTime.now()));
            }

            ReplicaResponse res = sauron.report(prev, request.getUuid(), observations);
            builder.setTimestamp(convertTimestamp(res.getTimestamp()));
            builder.setStatus(Silo.ResponseStatus.SUCCESS);
        } catch (InvalidIdentifierException e) {
            builder.setStatus(Silo.ResponseStatus.INVALID_IDENTIFIER);
            builder.setTimestamp(convertTimestamp(prev));
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void ctrlPing(Silo.CtrlPingRequest request, StreamObserver<Silo.CtrlPingResponse> responseObserver) {
        Silo.CtrlPingResponse.Builder builder = Silo.CtrlPingResponse.newBuilder();
        builder.setStatus(Silo.ResponseStatus.SUCCESS);
        List<Integer> prev = request.getTimestamp().getValueList();

        ReplicaResponse res = sauron.ctrlPing(prev);
        builder.setTimestamp(convertTimestamp(res.getTimestamp()));

        List<Silo.Camera> cameras = res.getCameras().stream()
            .map(Converter::convertToMessage)
            .collect(Collectors.toList());
        builder.addAllCameras(cameras);

        List<Silo.ObservationInfo> observations = res.getObservations().stream()
            .map(Converter::convertToMessage)
            .collect(Collectors.toList());
        builder.addAllObservations(observations);

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void ctrlInit(Silo.CtrlInitRequest request, StreamObserver<Silo.CtrlInitResponse> responseObserver) {
        sauron.ctrlInit();

        Silo.CtrlInitResponse.Builder builder = Silo.CtrlInitResponse
            .newBuilder()
            .setStatus(Silo.ResponseStatus.SUCCESS)
            .setTimestamp(convertTimestamp(new ArrayList<>()));

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void ctrlClear(Silo.CtrlClearRequest request, StreamObserver<Silo.CtrlClearResponse> responseObserver) {
        sauron.ctrlClear();

        Silo.CtrlClearResponse.Builder builder = Silo.CtrlClearResponse
            .newBuilder()
            .setStatus(Silo.ResponseStatus.SUCCESS)
            .setTimestamp(convertTimestamp(new ArrayList<>()));

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void gossip(Silo.GossipRequest request, StreamObserver<Silo.GossipResponse> responseObserver) {
        List<Integer> timestamp = request.getTimestamp().getValueList();
        List<ReplicaLog> log = new ArrayList<>();

        try {
            for (Silo.Log l : request.getLogList()) {
                List<Integer> prev = l.getPrev().getValueList();
                List<Integer> lts = l.getTimestamp().getValueList();

                if (l.hasCamera()) {
                    Coordinates coordinates = new Coordinates(l.getCamera().getCoordinates().getLatitude(), l.getCamera().getCoordinates().getLongitude());
                    Camera camera = new Camera(l.getCamera().getName(), coordinates);
                    log.add(new ReplicaLog(l.getInstance(), prev, lts, l.getUuid(), camera));
                } else if (!l.getObservationsList().isEmpty()) {
                    List<Observation> observations = new ArrayList<>();
                    for (Silo.Observation observation : l.getObservationsList()) {
                        ObservationType type = ObservationType.fromSilo.get(observation.getType());
                        Observation obs = new Observation(observation.getCameraName(), type, observation.getIdentifier(), LocalDateTime.now());
                        observations.add(obs);
                    }
                    log.add(new ReplicaLog(l.getInstance(), prev, lts, l.getUuid(), observations));
                }
            }
        } catch (InvalidCameraNameException|InvalidCameraCoordinatesException|InvalidIdentifierException ignored) {
            // Do nothing...
        }

        sauron.receiveGossip(timestamp, request.getInstance(), log);
        responseObserver.onNext(Silo.GossipResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
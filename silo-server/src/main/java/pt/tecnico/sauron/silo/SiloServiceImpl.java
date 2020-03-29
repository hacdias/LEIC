package pt.tecnico.sauron.silo;

import java.util.Map;
import io.grpc.stub.StreamObserver;

import pt.tecnico.sauron.silo.domain.ObservationType;
import pt.tecnico.sauron.silo.domain.PingInformation;
import pt.tecnico.sauron.silo.domain.Sauron;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraNameException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraCoordinatesException;
import pt.tecnico.sauron.silo.exceptions.DuplicateCameraException;
import pt.tecnico.sauron.silo.exceptions.InvalidIdentifierException;
import pt.tecnico.sauron.silo.exceptions.NoObservationException;
import pt.tecnico.sauron.silo.grpc.*;

public class SiloServiceImpl extends SauronGrpc.SauronImplBase {
    private Sauron sauron = new Sauron();

    private final Map<Silo.ObservationType, ObservationType> typesConverter = Map.ofEntries(
            Map.entry(Silo.ObservationType.PERSON, ObservationType.PERSON),
            Map.entry(Silo.ObservationType.CAR, ObservationType.CAR));

    @Override
    public void camJoin(Silo.CamJoinRequest request, StreamObserver<Silo.CamJoinResponse> responseObserver) {
        Silo.CamJoinResponse.Builder builder = Silo.CamJoinResponse.newBuilder();

        try {
            Silo.Camera cam = request.getCamera();
            Silo.Coordinates coordinates = cam.getCoordinates();
            sauron.addCamera(cam.getName(), coordinates.getLatitude(),coordinates.getLongitude());
        } catch (InvalidCameraNameException e) {
            builder.setStatus(Silo.ResponseStatus.INVALID_CAMERA_NAME);
        } catch (DuplicateCameraException e) {
            builder.setStatus(Silo.ResponseStatus.DUPLICATE_CAMERA);
        }catch (InvalidCameraCoordinatesException e) {
            builder.setStatus(Silo.ResponseStatus.INVALID_CAMERA_COORDINATES);
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
         
    }

    @Override
    public void camInfo(Silo.CamInfoRequest request, StreamObserver<Silo.CamInfoResponse> responseObserver) {
        Silo.CamInfoResponse.Builder builder = Silo.CamInfoResponse.newBuilder();

        try {
            sauron.getCamera(request.getName());
        } catch (InvalidCameraException e) {
            builder.setStatus(Silo.ResponseStatus.INVALID_CAMERA);
        } 

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
         
    }

    @Override
    public void track(Silo.TrackRequest request, StreamObserver<Silo.TrackResponse> responseObserver) {
        Silo.TrackResponse.Builder builder = Silo.TrackResponse.newBuilder();
        builder.setStatus(Silo.ResponseStatus.SUCCESS);

        try {
            ObservationType type = typesConverter.get(request.getType());
            sauron.track(type, request.getIdentifier());
        } catch (NoObservationException e) {
            builder.setStatus(Silo.ResponseStatus.NO_OBSERVATION_FOUND);
            // TODO: tell which of the identifiers wasn't found!
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void trackMatch(Silo.TrackMatchRequest request, StreamObserver<Silo.TrackMatchResponse> responseObserver) {
        Silo.TrackMatchResponse.Builder builder = Silo.TrackMatchResponse.newBuilder();
        builder.setStatus(Silo.ResponseStatus.SUCCESS);

        try {
            ObservationType type = typesConverter.get(request.getType());
            sauron.trackMatch(type, request.getPattern());
        } catch (NoObservationException e) {
            builder.setStatus(Silo.ResponseStatus.NO_OBSERVATION_FOUND);
            // TODO: tell which of the identifiers wasn't found!
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void trace(Silo.TraceRequest request, StreamObserver<Silo.TraceResponse> responseObserver) {
        Silo.TraceResponse.Builder builder = Silo.TraceResponse.newBuilder();
        builder.setStatus(Silo.ResponseStatus.SUCCESS);

        try {
            ObservationType type = typesConverter.get(request.getType());
            sauron.trace(type, request.getIdentifier());
        } catch (NoObservationException e) {
            builder.setStatus(Silo.ResponseStatus.NO_OBSERVATION_FOUND);
            // TODO: tell which of the identifiers wasn't found!
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void report(Silo.ReportRequest request, StreamObserver<Silo.ReportResponse> responseObserver) {
        Silo.ReportResponse.Builder builder = Silo.ReportResponse.newBuilder();

        try {
            for (Silo.Observation observation : request.getObservationsList()) {
                ObservationType type = typesConverter.get(observation.getType());
                sauron.report(request.getCameraName(), type, observation.getIdentifier());
            }
        } catch (InvalidCameraException e) {
            builder.setStatus(Silo.ResponseStatus.INVALID_CAMERA);
        } catch (InvalidIdentifierException e) {
            builder.setStatus(Silo.ResponseStatus.INVALID_IDENTIFIER);
            // TODO: tell which of the identifiers is invalid!
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void ctrlPing(Silo.CtrlPingRequest request, StreamObserver<Silo.CtrlPingResponse> responseObserver) {
        Silo.CtrlPingResponse.Builder builder = Silo.CtrlPingResponse.newBuilder();
        builder.setStatus(Silo.ResponseStatus.SUCCESS);

        PingInformation information = sauron.ctrlPing();
        builder.setCameras(information.getCamerasNumber());
        builder.setObservations(information.getObservationsNumber());

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void ctrlInit(Silo.CtrlInitRequest request, StreamObserver<Silo.CtrlInitResponse> responseObserver) {
        Silo.CtrlInitResponse.Builder builder = Silo.CtrlInitResponse.newBuilder();
        builder.setStatus(Silo.ResponseStatus.SUCCESS);

        // TODO: Send Information
        sauron.ctrlInit();

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void ctrlClear(Silo.CtrlClearRequest request, StreamObserver<Silo.CtrlClearResponse> responseObserver) {
        Silo.CtrlClearResponse.Builder builder = Silo.CtrlClearResponse.newBuilder();
        builder.setStatus(Silo.ResponseStatus.SUCCESS);

        sauron.ctrlClear();

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
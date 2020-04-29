package pt.tecnico.sauron.silo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Converter;
import pt.tecnico.sauron.silo.domain.Observation;
import pt.tecnico.sauron.silo.domain.ObservationType;
import pt.tecnico.sauron.silo.domain.PingInformation;
import pt.tecnico.sauron.silo.domain.ReplicaManager;
import pt.tecnico.sauron.silo.domain.Sauron;
import pt.tecnico.sauron.silo.exceptions.DuplicateCameraException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraCoordinatesException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraException;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraNameException;
import pt.tecnico.sauron.silo.exceptions.InvalidIdentifierException;
import pt.tecnico.sauron.silo.exceptions.NoObservationException;
import pt.tecnico.sauron.silo.grpc.SauronGrpc;
import pt.tecnico.sauron.silo.grpc.Silo;

public class SiloServiceImpl extends SauronGrpc.SauronImplBase {
  private ReplicaManager replicaManager;
  private Sauron sauron = new Sauron();

  public SiloServiceImpl(Integer instance, Integer numberServers) {
    super();
    this.replicaManager = new ReplicaManager(instance, numberServers);
  }

  private final Map<Silo.ObservationType, ObservationType> typesConverter = Map.ofEntries(
    Map.entry(Silo.ObservationType.PERSON, ObservationType.PERSON),
    Map.entry(Silo.ObservationType.CAR, ObservationType.CAR)
  );

  @Override
  public void camJoin(Silo.CamJoinRequest request, StreamObserver<Silo.CamJoinResponse> responseObserver) {
    Silo.CamJoinResponse.Builder builder = Silo.CamJoinResponse.newBuilder();
    builder.setStatus(Silo.ResponseStatus.SUCCESS);

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
      Camera cam = sauron.getCamera(request.getName());
      builder.setCoordinates(Converter.convertToMessage(cam.getCoordinates()));
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
      Observation observation = sauron.track(type, request.getIdentifier());

      Silo.ObservationInfo observationInfo = Converter.convertToMessage(observation);
      builder.setObservation(observationInfo);
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
      List<Observation> observations = sauron.trackMatch(type, request.getPattern());

      List<Silo.ObservationInfo> observationsInfo = observations.stream()
        .map(element -> Converter.convertToMessage(element))
        .collect(Collectors.toList());

      builder.addAllObservations(observationsInfo);
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
      List<Observation> observations = sauron.trace(type, request.getIdentifier());

      List<Silo.ObservationInfo> observationInfos = observations.stream()
        .map(element -> Converter.convertToMessage(element))
        .collect(Collectors.toList());

      builder.addAllObservations(observationInfos);

    } catch (NoObservationException e) {
      builder.setStatus(Silo.ResponseStatus.NO_OBSERVATION_FOUND);
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
    }

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void ctrlPing(Silo.CtrlPingRequest request, StreamObserver<Silo.CtrlPingResponse> responseObserver) {
    Silo.CtrlPingResponse.Builder builder = Silo.CtrlPingResponse.newBuilder();
    builder.setStatus(Silo.ResponseStatus.SUCCESS);

    PingInformation information = sauron.ctrlPing();

    List<Silo.Camera> cameras = information.getCameras().stream()
      .map(element -> Converter.convertToMessage(element))
      .collect(Collectors.toList());
    builder.addAllCameras(cameras);

    List<Silo.ObservationInfo> observations = information.getObservations().stream()
      .map(element -> Converter.convertToMessage(element))
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
      .setStatus(Silo.ResponseStatus.SUCCESS);

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void ctrlClear(Silo.CtrlClearRequest request, StreamObserver<Silo.CtrlClearResponse> responseObserver) {
    sauron.ctrlClear();

    Silo.CtrlClearResponse.Builder builder = Silo.CtrlClearResponse
      .newBuilder()
      .setStatus(Silo.ResponseStatus.SUCCESS);

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }
}
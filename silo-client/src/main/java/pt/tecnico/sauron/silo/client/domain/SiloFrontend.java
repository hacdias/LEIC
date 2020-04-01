package pt.tecnico.sauron.silo.client.domain;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
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

public class SiloFrontend implements AutoCloseable {
  private String serverAddress;
  private Integer serverPort;
  ManagedChannel channel;
  SauronGrpc.SauronBlockingStub stub;

  public SiloFrontend(String address, Integer port) {
    serverAddress = address;
    serverPort = port;
    String target = serverAddress + ":" + serverPort;
    channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
    stub = SauronGrpc.newBlockingStub(channel);
  }

  private final Map<ObservationType, Silo.ObservationType> typesConverter = Map.ofEntries(
    Map.entry(ObservationType.PERSON, Silo.ObservationType.PERSON),
    Map.entry(ObservationType.CAR, Silo.ObservationType.CAR)
  );

  public Status ping() throws SauronClientException {
    CtrlPingRequest request = CtrlPingRequest.newBuilder().build();
    CtrlPingResponse response = stub.ctrlPing(request);

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
    CtrlClearRequest request = CtrlClearRequest.newBuilder().build();
    CtrlClearResponse response = stub.ctrlClear(request);
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
      .setCamera(camera)
      .build();

    CamJoinResponse response = stub.camJoin(request);
    throwIfNotSuccess(response.getStatus());
  }

  public Coordinates camInfo(String name) throws SauronClientException {
    CamInfoRequest request = CamInfoRequest.newBuilder()
      .setName(name)
      .build();

    CamInfoResponse response = stub.camInfo(request);
    throwIfNotSuccess(response.getStatus());
    return new Coordinates(response.getCoordinates());
  }

  public void report(String camName, List<Observation> observations) throws SauronClientException {
    List<Silo.Observation> siloObservations = observations
      .stream()
      .map(observation -> {
        Instant instant = observation.getDatetime().atZone(ZoneId.systemDefault()).toInstant();

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
      .setCameraName(camName)
      .addAllObservations(siloObservations)
      .build();

    ReportResponse response = stub.report(request);
    throwIfNotSuccess(response.getStatus());
  }

  public Observation track(ObservationType type, String identifier) throws SauronClientException {
    TrackRequest request = TrackRequest.newBuilder()
      .setType(typesConverter.get(type))
      .setIdentifier(identifier)
      .build();

    TrackResponse response = stub.track(request);
    throwIfNotSuccess(response.getStatus());
    return new Observation(response.getObservation().getCamera(), response.getObservation().getObservation());
  }

  public List<Observation> trackMatch(ObservationType type, String pattern) throws SauronClientException {
    TrackMatchRequest request = TrackMatchRequest.newBuilder()
      .setType(typesConverter.get(type))
      .setPattern(pattern)
      .build();

    TrackMatchResponse response = stub.trackMatch(request);
    throwIfNotSuccess(response.getStatus());

    return response.getObservationsList()
      .stream()
      .map(info -> new Observation(info.getCamera(), info.getObservation()))
      .collect(Collectors.toList());
  }

  public List<Observation> trace(ObservationType type, String identifier) throws SauronClientException {
    TraceRequest request = TraceRequest.newBuilder()
      .setType(typesConverter.get(type))
      .setIdentifier(identifier)
      .build();

    TraceResponse response = stub.trace(request);
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
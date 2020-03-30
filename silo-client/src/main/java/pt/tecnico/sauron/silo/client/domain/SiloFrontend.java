package pt.tecnico.sauron.silo.client.domain;

import java.util.List;
import java.util.stream.Collectors;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.grpc.*;
import pt.tecnico.sauron.silo.grpc.Silo.*;

public class SiloFrontend {
    private String serverAdress;
    private Integer serverPort;
    ManagedChannel channel;
    SauronGrpc.SauronBlockingStub stub;

    public SiloFrontend(String adress, Integer port) {
        serverAdress = adress;
        serverPort = port;

		String target = serverAdress + ":" + serverPort;
		System.out.println("Target: " + target);

		channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		stub = SauronGrpc.newBlockingStub(channel);
    }

    public String ping() {
        CtrlPingRequest request = CtrlPingRequest.newBuilder().build();
        CtrlPingResponse response = stub.ctrlPing(request);

        String text;
        if (response.getStatus() == Silo.ResponseStatus.SUCCESS) {
            List<Silo.Camera> cameras = response.getCamerasList();
            List<Silo.ObservationInfo> observations = response.getObservationsList();

            text = "The server pinged back!\n";
            text = text.concat("Number of Cameras: " +  cameras.size() + "\n");
            cameras.stream().map(element -> {
                String subText;
                subText = "Name: " + element.getName() + " : ";
                subText = subText.concat("Latitude: " + element.getCoordinates().getLatitude() + " : ");
                subText = subText.concat("Longitude: " + element.getCoordinates().getLatitude());
                return subText;
            }).collect(Collectors.joining("\n"));

            text = text.concat("\nNumber of Observations: " + observations.size());
            observations.stream().map(element -> {
                String subText = "";
                if (element.getObservation().getType() == Silo.ObservationType.CAR) subText = "car" + ",";
                else if (element.getObservation().getType() == Silo.ObservationType.PERSON) subText = "person" + ",";
                
                subText = subText.concat(element.getObservation().getIdentifier() + ",");
                subText = subText.concat(element.getObservation().getTimestamp().toString() + ",");
                subText = subText.concat(element.getCamera().getName() + ",");
                subText = subText.concat(element.getCamera().getCoordinates().getLatitude() + ",");
                subText = subText.concat(String.valueOf(element.getCamera().getCoordinates().getLongitude()));
                return subText;
            }).collect(Collectors.joining("\n"));
            
        } else {
            text = "There was a error with the command!";
        }

        return text;
    }

    public String init() {
        // TODO
        return null;
    }

    public String clear() {
        CtrlClearRequest request = CtrlClearRequest.newBuilder().build();
        CtrlClearResponse response = stub.ctrlClear(request);

        String text;
        if (response.getStatus() == Silo.ResponseStatus.SUCCESS) {
            text = "The server was cleared!\n";
        } else {
            text = "There was a error with the command!";
        }

        return text;
    }

    public CamJoinResponse camJoin(String name, Float latitude, Float longitude) {
        Coordinates coordinates = Coordinates.newBuilder()
            .setLatitude(latitude)
            .setLongitude(longitude)
            .build();
        Camera camera = Camera.newBuilder()
            .setName(name)
            .setCoordinates(coordinates)
            .build();

        CamJoinRequest request = CamJoinRequest.newBuilder()
            .setCamera(camera)
            .build();

        return stub.camJoin(request);
    }

    public CamInfoResponse camInfo(String name) {
        CamInfoRequest request = CamInfoRequest.newBuilder()
            .setName(name)
            .build();

        return stub.camInfo(request);
    }

    public ReportResponse report(String camName, List<Observation> observations) {
        ReportRequest request = ReportRequest.newBuilder()
            .setCameraName(camName)
            .addAllObservations(observations)
            .build();

        return stub.report(request);
    }

    public TrackResponse track(ObservationType type, String identifier) {
        TrackRequest request = TrackRequest.newBuilder()
            .setType(type)
            .setIdentifier(identifier)
            .build();
        
        return stub.track(request);
    }

    public TrackMatchResponse trackMatch(ObservationType type, String pattern) {
        TrackMatchRequest request = TrackMatchRequest.newBuilder()
            .setType(type)
            .setPattern(pattern)
            .build();
        
        return stub.trackMatch(request);
    }

    public TraceResponse trace(ObservationType type, String identifier) {
        TraceRequest request = TraceRequest.newBuilder()
            .setType(type)
            .setIdentifier(identifier)
            .build();
        
        return stub.trace(request);
    }


}
package pt.tecnico.sauron.silo.client.domain;

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
            Integer numberCameras = response.getCameras();
            Integer numberObservations = response.getObservations();

            text = "The server pinged back!\n";
            text = text.concat("Number of Cameras: " +  numberCameras.toString() + "\n");
            text = text.concat("Number of Observations: " + numberObservations.toString());
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
}
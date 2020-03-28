package pt.tecnico.sauron.silo;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.grpc.*;

public class SiloServiceImpl extends SauronGrpc.SauronImplBase{

    private Sauron sauron = new Sauron();

    @Override
    public void camJoin(Silo.CamJoinRequest request, StreamObserver<Silo.CamJoinResponse> responseObserver){

        sauron.addCamera(request.getCamera());
        Silo.CamJoinResponse response = Silo.CamJoinResponse.newBuilder().build();
        
		responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void camInfo(Silo.CamInfoRequest request, StreamObserver<Silo.CamInfoResponse> responseObserver){

        Silo.CamInfoResponse response = Silo.CamInfoResponse.newBuilder().setCoordinates(sauron.getCamInfo(request.getName())).build();
        
		responseObserver.onNext(response);

		responseObserver.onCompleted();

    }

    @Override
    public void track(Silo.TrackRequest request, StreamObserver<Silo.TrackResponse> responseObserver) {

        Silo.TrackResponse response = Silo.TrackResponse.newBuilder().setObservation(sauron.track(request.getType(), request.getIdentifier())).build();

        responseObserver.onNext(response);

		responseObserver.onCompleted();
    }

    @Override
    public void trackMatch(Silo.TrackMatchRequest request, StreamObserver<Silo.TrackMatchResponse> responseObserver) {

        Silo.TrackMatchResponse response = Silo.TrackMatchResponse.newBuilder().setObservation(sauron.trackMatch(request.getType(), request.getPartIdentifier())).build();

        responseObserver.onNext(response);

		responseObserver.onCompleted();
    }

    @Override
    public void trace(Silo.TraceRequest request, StreamObserver<Silo.TraceResponse> responseObserver) {

        Silo.TraceResponse response = Silo.TraceResponse.newBuilder().addAllObservations(sauron.trace(request.getType(), request.getIdentifier())).build();

        responseObserver.onNext(response);

		responseObserver.onCompleted();
    }
}
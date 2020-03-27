package pt.tecnico.sauron.silo;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.grpc.*;

public class SiloServiceImpl extends SauronGrpc.SauronImplBase{

    private Sauron sauron = new Sauron();

    @Override
    public void CamJoin(Silo.CamJoinRequest request, StreamObserver<Silo.CamJoinResponse> responseObserver){

        sauron.addCamera(request.getCamera());
        Silo.CamJoinResponse response = Silo.CamJoinResponse.newBuilder().build();
        
		responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void CamInfo(Silo.CamInfoRequest request,StreamObserver<Silo.CamInfoResponse> responseObserver){

        Silo.CamInfoResponse response = Silo.CamInfoResponse.newBuilder().setCoordinates(sauron.getCamInfo(request.getName())).build();
        
		responseObserver.onNext(response);

		responseObserver.onCompleted();

    }
}
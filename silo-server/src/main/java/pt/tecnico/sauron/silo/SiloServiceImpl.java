package pt.tecnico.sauron.silo;

import java.util.Map;
import io.grpc.stub.StreamObserver;

import pt.tecnico.sauron.silo.domain.ObservationType;
import pt.tecnico.sauron.silo.domain.Sauron;
import pt.tecnico.sauron.silo.exceptions.InvalidCameraException;
import pt.tecnico.sauron.silo.exceptions.InvalidIdentifierException;
import pt.tecnico.sauron.silo.grpc.*;

public class SiloServiceImpl extends SauronGrpc.SauronImplBase {
    private Sauron sauron = new Sauron();

    private final Map<Silo.ObservationType, ObservationType> typesConverter = Map.ofEntries(
            Map.entry(Silo.ObservationType.PERSON, ObservationType.PERSON),
            Map.entry(Silo.ObservationType.CAR, ObservationType.CAR));

    @Override
    public void camJoin(Silo.CamJoinRequest request, StreamObserver<Silo.CamJoinResponse> responseObserver) {

        /*
         * sauron.addCamera(request.getCamera()); Silo.CamJoinResponse response =
         * Silo.CamJoinResponse.newBuilder().build();
         *
         * responseObserver.onNext(response);
         *
         * responseObserver.onCompleted();
         */
    }

    @Override
    public void camInfo(Silo.CamInfoRequest request, StreamObserver<Silo.CamInfoResponse> responseObserver) {

        /*
         * Silo.CamInfoResponse response =
         * Silo.CamInfoResponse.newBuilder().setCoordinates(sauron.getCamInfo(request.
         * getName())).build();
         *
         * responseObserver.onNext(response);
         *
         * responseObserver.onCompleted();
         */

    }

    @Override
    public void track(Silo.TrackRequest request, StreamObserver<Silo.TrackResponse> responseObserver) {

        /*
         * Silo.TrackResponse response =
         * Silo.TrackResponse.newBuilder().setObservation(sauron.track(request.getType()
         * , request.getIdentifier())).build();
         *
         * responseObserver.onNext(response);
         *
         * responseObserver.onCompleted();
         */
    }

    @Override
    public void trackMatch(Silo.TrackMatchRequest request, StreamObserver<Silo.TrackMatchResponse> responseObserver) {

        /*
         * Silo.TrackMatchResponse response =
         * Silo.TrackMatchResponse.newBuilder().setObservation(sauron.trackMatch(request
         * .getType(), request.getPartIdentifier())).build();
         *
         * responseObserver.onNext(response);
         *
         * responseObserver.onCompleted();
         */
    }

    @Override
    public void trace(Silo.TraceRequest request, StreamObserver<Silo.TraceResponse> responseObserver) {

        /*
         * Silo.TraceResponse response =
         * Silo.TraceResponse.newBuilder().addAllObservations(sauron.trace(request.
         * getType(), request.getIdentifier())).build();
         *
         * responseObserver.onNext(response);
         *
         * responseObserver.onCompleted();
         */
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
}
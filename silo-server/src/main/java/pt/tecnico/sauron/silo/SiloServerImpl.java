package pt.tecnico.sauron.silo;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Observation;
import pt.tecnico.sauron.silo.grpc.*;


public class SiloServerImpl extends SiloGrpc.SiloImplBase{

    private SiloServerOps Ops = new SiloServerOps();

    @Override
    public void camJoin(SiloOuterClass.CamJoinRequest request, StreamObserver<SiloOuterClass.CamJoinResponse> responseObserver) {
        // StreamObserver is used to represent the gRPC stream between the server and
        // client in order to send the appropriate responses (or errors, if any occur).

        String localName = request.getLocal();
        float locationX = request.getLatitude();
        float locationY = request.getLongitude();
        // add camera to server ops
        SiloOuterClass.CamJoinResponse response = SiloOuterClass.CamJoinResponse.newBuilder().setResult(Ops.camJoin(localName, locationX, locationY)).build();

        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }

    @Override
    public void camInfo(SiloOuterClass.CamInfoRequest request, StreamObserver<SiloOuterClass.CamInfoResponse> responseObserver) {
        // StreamObserver is used to represent the gRPC stream between the server and
        // client in order to send the appropriate responses (or errors, if any occur).

        String localName = request.getLocal();

        Camera cam_location = Ops.camInfo(localName);

        float locationX = cam_location.getLatitude();
        float locationY = cam_location.getLongitude();


        SiloOuterClass.CamInfoResponse response = SiloOuterClass.CamInfoResponse.newBuilder().setLatitude(locationX).setLongitude(locationY).build();

        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }

    @Override
    public void report(SiloOuterClass.ReportRequest request, StreamObserver<SiloOuterClass.ReportResponse> responseObserver) {
        // StreamObserver is used to represent the gRPC stream between the server and
        // client in order to send the appropriate responses (or errors, if any occur).

        String camName = request.getCamName();
        String id = request.getId();

        // verificacao de argumentos aqui

        if (request.getType().equals(SiloOuterClass.ObjectType.PERSON)){
            Ops.report(camName, id, "person");

            SiloOuterClass.ReportResponse response = SiloOuterClass.ReportResponse.newBuilder().setError(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } else if (request.getType().equals(SiloOuterClass.ObjectType.CAR)) {
            Ops.report(camName, id, "car");
            SiloOuterClass.ReportResponse response = SiloOuterClass.ReportResponse.newBuilder().setError(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } else {
            SiloOuterClass.ReportResponse response = SiloOuterClass.ReportResponse.newBuilder().setError(false).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            // excepcao
        }


    }
}

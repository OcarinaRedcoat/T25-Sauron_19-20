package pt.tecnico.sauron.silo;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Observation;
import pt.tecnico.sauron.silo.exceptions.BadEntryException;
import pt.tecnico.sauron.silo.grpc.*;

import java.util.ArrayList;
import java.util.List;
import static io.grpc.Status.INVALID_ARGUMENT;




public class SiloServerImpl extends SiloGrpc.SiloImplBase{

    private SiloServerOps Ops = new SiloServerOps();

    @Override
    public void camJoin(SiloOuterClass.CamJoinRequest request, StreamObserver<SiloOuterClass.CamJoinResponse> responseObserver) {
        // StreamObserver is used to represent the gRPC stream between the server and
        // client in order to send the appropriate responses (or errors, if any occur).

        String localName = request.getLocal();
        float locationX = request.getLatitude();
        float locationY = request.getLongitude();

        try{
            Ops.camJoin(localName, locationX, locationY);
        } catch (BadEntryException e){
            System.out.println(e.toString());
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
        }
        SiloOuterClass.CamJoinResponse response = SiloOuterClass.CamJoinResponse.newBuilder().build();
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


        try{
            Ops.report(camName, id, request.getType());
            SiloOuterClass.ReportResponse response = SiloOuterClass.ReportResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BadEntryException e){
            System.out.println(e.toString());
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
        }


    }

    public void track(SiloOuterClass.TrackRequest request, StreamObserver<SiloOuterClass.TrackResponse> responseObserver) {


        try{
            Observation trackedObs = Ops.track(request.getType(), request.getId());


            Camera cam = Ops.camInfo(trackedObs.getCamera());

            SiloOuterClass.Camera camera = SiloOuterClass.Camera.newBuilder().setName(trackedObs.getCamera()).setLatitude(cam.getLatitude()).setLongitude(cam.getLongitude()).build();

            SiloOuterClass.Observation obs = SiloOuterClass.Observation.newBuilder().setId(trackedObs.getId()).setType(trackedObs.getType()).setCam(camera).setTimestamp(trackedObs.getTimestamp().toString()).build();



            SiloOuterClass.TrackResponse response = SiloOuterClass.TrackResponse.newBuilder().setObsRes(obs).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BadEntryException e){
            System.out.println(e.toString());
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
        }

    }

    public void trackMatch(SiloOuterClass.TrackMatchRequest request, StreamObserver<SiloOuterClass.TrackMatchResponse> responseObserver) {


        try{

            List<Observation> obsResponse = Ops.trackMatch(request.getType(), request.getId());


            List<SiloOuterClass.Observation> obsRes = new ArrayList<>();
            for (Observation o: obsResponse){

                Camera cam = Ops.camInfo(o.getCamera());

                SiloOuterClass.Camera camera = SiloOuterClass.Camera.newBuilder().setName(o.getCamera()).setLatitude(cam.getLatitude()).setLongitude(cam.getLongitude()).build();


                SiloOuterClass.Observation obs = SiloOuterClass.Observation.newBuilder().setId(o.getId()).setCam(camera).setType(o.getType()).setTimestamp(o.getTimestamp().toString()).build();

                obsRes.add(obs);

            }
            SiloOuterClass.TrackMatchResponse response = SiloOuterClass.TrackMatchResponse.newBuilder().addAllObsRes(obsRes).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();


        } catch (BadEntryException e){
            System.out.println(e.toString());
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
        }
    }

    public void trace(SiloOuterClass.TraceRequest request, StreamObserver<SiloOuterClass.TraceResponse> responseObserver){

        try{
            List<Observation> obsResponse = Ops.trace(request.getType(), request.getId());


            SiloOuterClass.TraceResponse builder = null;

            List<SiloOuterClass.Observation> obsLst = new ArrayList<>();
            for (Observation o: obsResponse){

                Camera cam = Ops.camInfo(o.getCamera());

                SiloOuterClass.Camera camera = SiloOuterClass.Camera.newBuilder().setName(o.getCamera()).setLatitude(cam.getLatitude()).setLongitude(cam.getLongitude()).build();

                SiloOuterClass.Observation obs = SiloOuterClass.Observation.newBuilder().setId(o.getId()).setCam(camera).setType(o.getType()).setTimestamp(o.getTimestamp().toString()).build();

                obsLst.add(obs);
            }
            SiloOuterClass.TraceResponse response = SiloOuterClass.TraceResponse.newBuilder().addAllObsRes(obsLst).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (BadEntryException e){
            System.out.println(e.toString());
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
        }

    }

    public void ctrlPing(SiloOuterClass.PingRequest request, StreamObserver<SiloOuterClass.PingResponse> responseObserver){

        String input = request.getPing();
        String output = "Server Running " + input;

        if (input == null || input.isBlank()) {
            responseObserver.onError(INVALID_ARGUMENT
                    .withDescription("Input cannot be empty!").asRuntimeException());
        }

        SiloOuterClass.PingResponse response = SiloOuterClass.PingResponse.newBuilder().
                setPong(output).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }




    public void ctrlClear(SiloOuterClass.ClearRequest request, StreamObserver<SiloOuterClass.ClearResponse> responseObserver) {


        Ops.clearAll();
        SiloOuterClass.ClearResponse response = SiloOuterClass.ClearResponse.newBuilder().build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    /*public void ctrlInit(SiloOuterClass.InitRequest request, StreamObserver<SiloOuterClass.InitResponse> responseObserver) {

        String host = request.getLocalhost();
        SiloOuterClass.InitResponse response = SiloOuterClass.InitResponse.newBuilder().setHost(host).setPort().build():

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }*/


}


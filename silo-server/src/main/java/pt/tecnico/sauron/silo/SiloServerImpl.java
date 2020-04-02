package pt.tecnico.sauron.silo;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Observation;
import pt.tecnico.sauron.silo.grpc.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;


public class SiloServerImpl extends SiloGrpc.SiloImplBase{

    private SiloServerOps Ops = new SiloServerOps();

    @Override
    public void camJoin(SiloOuterClass.CamJoinRequest request, StreamObserver<SiloOuterClass.CamJoinResponse> responseObserver) throws IllegalArgumentException {
        // StreamObserver is used to represent the gRPC stream between the server and
        // client in order to send the appropriate responses (or errors, if any occur).

        String localName = request.getLocal();
        float locationX = request.getLatitude();
        float locationY = request.getLongitude();

        try{
            Ops.camJoin(localName, locationX, locationY);
        } catch (IllegalArgumentException e){
            throw e;
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
    public void report(SiloOuterClass.ReportRequest request, StreamObserver<SiloOuterClass.ReportResponse> responseObserver) throws IllegalArgumentException {
        // StreamObserver is used to represent the gRPC stream between the server and
        // client in order to send the appropriate responses (or errors, if any occur).

        String camName = request.getCamName();
        String id = request.getId();


        try{
            Ops.report(camName, id, request.getType());
            SiloOuterClass.ReportResponse response = SiloOuterClass.ReportResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e){
            throw e;
        }


    }

    public void track(SiloOuterClass.TrackRequest request, StreamObserver<SiloOuterClass.TrackResponse> responseObserver) throws IllegalArgumentException{

        try{
            Observation trackedObs = Ops.track(request.getType(), request.getId());


            Camera cam = Ops.camInfo(trackedObs.getCamera());

            SiloOuterClass.Camera camera = SiloOuterClass.Camera.newBuilder().setName(trackedObs.getCamera()).setLatitude(cam.getLatitude()).setLongitude(cam.getLongitude()).build();

            //Timestamp tmp = Timestamp.newBuilder().setSeconds(trackedObs.getTimestamp().getEpochSecond()).build();
            SiloOuterClass.Observation obs = SiloOuterClass.Observation.newBuilder().setId(trackedObs.getId()).setType(trackedObs.getType()).setCam(camera).setTimestamp(trackedObs.getTimestamp().toString()).build();



            SiloOuterClass.TrackResponse response = SiloOuterClass.TrackResponse.newBuilder().setObsRes(obs).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e){
            throw e;
        }

    }

    public void trackMatch(SiloOuterClass.TrackMatchRequest request, StreamObserver<SiloOuterClass.TrackMatchResponse> responseObserver) throws IllegalArgumentException {


        try{

            List<Observation> obsResponse = Ops.trackMatch(request.getType(), request.getId());

            SiloOuterClass.TrackMatchResponse builder = null;

            for (Observation o: obsResponse){

                Camera cam = Ops.camInfo(o.getCamera());

                SiloOuterClass.Camera camera = SiloOuterClass.Camera.newBuilder().setName(o.getCamera()).setLatitude(cam.getLatitude()).setLongitude(cam.getLongitude()).build();


                SiloOuterClass.Observation obs = SiloOuterClass.Observation.newBuilder().setId(o.getId()).setCam(camera).setType(o.getType()).setTimestamp(o.getTimestamp().toString()).build();

                builder = SiloOuterClass.TrackMatchResponse.newBuilder().addObsRes(obs).build();
/*            SiloOuterClass.TrackMatchResponse builder = SiloOuterClass.TrackMatchResponse.newBuilder().addAllObsRes(new ArrayList<SiloOuterClass.Observation>()).build();

            SiloOuterClass.TrackMatchResponse response = SiloOuterClass.TrackMatchResponse.newBuilder().addAllObsRes(obsResponse)
            for (Observation o: obsResponse){

                Camera cam = Ops.camInfo(o.getCamera());

                SiloOuterClass.Camera camera = SiloOuterClass.Camera.newBuilder().setName(o.getCamera()).setLatitude(cam.getLatitude()).setLongitude(cam.getLongitude()).build();


                SiloOuterClass.Observation obs = SiloOuterClass.Observation.newBuilder().setId(o.getId()).setCam(camera).setType(o.getType()).setTimestamp(o.getTimestamp().toString()).build();

                builder.getObsResList().add(obs);
*/
            }

            responseObserver.onNext(builder);
            responseObserver.onCompleted();


        } catch (IllegalArgumentException e){
            throw e;
        }
    }

    public void trace(SiloOuterClass.TraceRequest request, StreamObserver<SiloOuterClass.TraceResponse> responseObserver) throws IllegalArgumentException{

        try{
            List<Observation> obsResponse = Ops.trace(request.getType(), request.getId());


            //SiloOuterClass.TraceResponse builder = SiloOuterClass.TraceResponse.newBuilder().addAllObsRes(new ArrayList<SiloOuterClass.Observation>()).build();
            SiloOuterClass.TraceResponse builder = null;


            for (Observation o: obsResponse){

                Camera cam = Ops.camInfo(o.getCamera());

                SiloOuterClass.Camera camera = SiloOuterClass.Camera.newBuilder().setName(o.getCamera()).setLatitude(cam.getLatitude()).setLongitude(cam.getLongitude()).build();


                SiloOuterClass.Observation obs = SiloOuterClass.Observation.newBuilder().setId(o.getId()).setCam(camera).setType(o.getType()).setTimestamp(o.getTimestamp().toString()).build();
                builder = SiloOuterClass.TraceResponse.newBuilder().addObsRes(obs).build();
                //builder.getObsResList().add(obs);

            }

            responseObserver.onNext(builder);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e){
            throw e;
        }

    }

}


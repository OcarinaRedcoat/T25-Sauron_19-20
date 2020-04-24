package pt.tecnico.sauron.silo;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Observation;
import pt.tecnico.sauron.silo.exceptions.BadEntryException;
import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
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
        String locationX = request.getLatitude();
        String locationY = request.getLongitude();

        try{
            Ops.camJoin(localName, locationX, locationY);
            SiloOuterClass.CamJoinResponse response = SiloOuterClass.CamJoinResponse.newBuilder().build();
            // Send a single response through the stream.
            responseObserver.onNext(response);
            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();
        } catch (BadEntryException e){
            System.out.println(e.getErrorMessage());
            if (e.getErrorMessage().equals(ErrorMessage.CAM_NAME_ALREADY_EXIST)) {
                responseObserver.onError(Status.ALREADY_EXISTS.withDescription(e.toString()).asRuntimeException());
            } else {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
            }
        }
        //SiloOuterClass.CamJoinResponse response = SiloOuterClass.CamJoinResponse.newBuilder().build();
        // Send a single response through the stream.
        //responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        //responseObserver.onCompleted();
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
    public void report(SiloOuterClass.ReportLot request, StreamObserver<SiloOuterClass.ReportResponse> responseObserver) {
        // StreamObserver is used to represent the gRPC stream between the server and
        // client in order to send the appropriate responses (or errors, if any occur).

        //String camName = request.getCamName();
        //List<String> id = request.getId();
        List<SiloOuterClass.ReportRequest> list = request.getReportLotList();

        List<String> camLot = new ArrayList<>();
        List<String> idLot = new ArrayList<>();
        List<SiloOuterClass.ObjectType> typeLot = new ArrayList<>();
        for (SiloOuterClass.ReportRequest lotto: list) {

            camLot.add(lotto.getCamName());
            idLot.add(lotto.getId());
            typeLot.add(lotto.getType());

        }

        try {
            Ops.report(camLot, idLot, typeLot);
            SiloOuterClass.ReportResponse response = SiloOuterClass.ReportResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BadEntryException e) {
            System.out.println(e.getErrorMessage());
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
            System.out.println(e.getErrorMessage());
            if (e.getErrorMessage().equals(ErrorMessage.ID_NOT_VALID) || (e.getErrorMessage().equals(ErrorMessage.ID_FOUND_WRONG_TYPE))) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
            } else {
                responseObserver.onError(Status.NOT_FOUND.withDescription(e.toString()).asRuntimeException());
            }
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
            System.out.println(e.getErrorMessage());
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.toString()).asRuntimeException());
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

        } catch (BadEntryException e) {
            System.out.println(e.getErrorMessage());
            if (e.getErrorMessage().equals(ErrorMessage.ID_NOT_VALID)) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
            } else {
                responseObserver.onError(Status.NOT_FOUND.withDescription(e.toString()).asRuntimeException());
            }
        }
    }

    public void ctrlPing(SiloOuterClass.PingRequest request, StreamObserver<SiloOuterClass.PongResponse> responseObserver){

        try{
            String pong = Ops.ping(request.getPing());
            SiloOuterClass.PongResponse response = SiloOuterClass.PongResponse.newBuilder().
                    setPong(pong).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (BadEntryException e){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
        }

    }




    public void ctrlClear(SiloOuterClass.ClearRequest request, StreamObserver<SiloOuterClass.ClearResponse> responseObserver) {


        Ops.clearAll();

        responseObserver.onNext(SiloOuterClass.ClearResponse.getDefaultInstance());
        responseObserver.onCompleted();

    }


    public void ctrlInit(SiloOuterClass.InitRequest request, StreamObserver<SiloOuterClass.InitResponse> responseObserver) {

        Ops.init();

        responseObserver.onNext(SiloOuterClass.InitResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }


}


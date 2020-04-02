package pt.tecnico.sauron.silo.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.grpc.SiloGrpc;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass;

import java.util.List;

public class SiloFrontend {

    private  SiloGrpc.SiloBlockingStub stub;

    public SiloFrontend() {}

    public ManagedChannel createChannel(String host, String portStr) {

        System.out.println("FE DEBUGGING...");
        final int port = Integer.parseInt(portStr);
        final String target = host + ":" + port;

        // Channel is the abstraction to connect to a service endpoint.
        // Let us use plaintext communication because we do not have certificates.
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        // It is up to the client to determine whether to block the call.
        // Here we create a blocking stub, but an async stub, or an async stub with
        // Future are also available.
        stub = SiloGrpc.newBlockingStub(channel);

        return channel;

    }

    /**
     * Arguments: name (String), and 2 locations X and Y (floats)
     * Converts the location X and Y to latitude and longitude and create a camera in Silo-server
     * This method is to be called only in eye
     */
    public void camJoin(String name, float locationX, float locationY){

        stub.camJoin(SiloOuterClass.CamJoinRequest.newBuilder().setLocal(name).setLatitude(locationX).setLongitude(locationY).build());
    }

    /**
     * Arguments: name (String)
     * Asks the server for the coordinates of a camera that has the given name
     * Returns the coordinates in a float array size 2
     */

    public float[] camInfo(String camName){

        //CamInfoResponse is a pair of floats
        SiloOuterClass.CamInfoResponse response = stub.camInfo(SiloOuterClass.CamInfoRequest.newBuilder().setLocal(camName).build());
        float []coords = new float[2];
        coords[0] = response.getLatitude(); coords[1] = response.getLongitude();
        return coords;
    }
    /**
     * Arguments: type (String) id (String) camName (camName)
     * Receives the name of the camera, a set of observations, and the corresponding data.
     * The name must correspond to a previously registered camera.
     * The server records the observations with their date and time, at the time of receipt;
     */
    public void report(String type, String id, String camName){

        SiloOuterClass.ObjectType requestType;
        if (type.equals("person")){

            requestType = SiloOuterClass.ObjectType.person;
            stub.report(SiloOuterClass.ReportRequest.newBuilder().setType(requestType).setId(id).setCamName(camName).build());

        } else if (type.equals("car")){

            requestType = SiloOuterClass.ObjectType.car;
            stub.report(SiloOuterClass.ReportRequest.newBuilder().setType(requestType).setId(id).setCamName(camName).build());

        }
    }

    /**
     * Arguments: name (String) id (String)
     * Receives the type of object to find and the identifier of the object sought.
     * Returns the most recent observation of the searched object;
     */
    public String track(String type, String id){

        SiloOuterClass.ObjectType requestType;
        SiloOuterClass.TrackResponse response;

        if (type.equals("person")){
            requestType = SiloOuterClass.ObjectType.person;
            response = stub.track(SiloOuterClass.TrackRequest.newBuilder().setType(requestType).setId(id).build());
            return type + ',' + response.getObsRes().getId() + ',' + response.getObsRes().getTimestamp() + ',' + response.getObsRes().getCam().getName() + ',' + response.getObsRes().getCam().getLatitude() + ',' + response.getObsRes().getCam().getLongitude();
        } else if (type.equals("car")) {
            requestType = SiloOuterClass.ObjectType.car;
            response = stub.track(SiloOuterClass.TrackRequest.newBuilder().setType(requestType).setId(id).build());
            return type + ',' + response.getObsRes().getId() + ',' +  response.getObsRes().getTimestamp() + ',' + response.getObsRes().getCam().getName() + ',' + response.getObsRes().getCam().getLatitude() + ',' + response.getObsRes().getCam().getLongitude();

        }

        return ""; // se chegou aqui significa que nao recebeu nada e os argumentos estao mal
    }

    /**
     * Arguments:
     * Allows you to locate an observed object with part of its identifier.
     * Receives the type of object to find and part of the identifier of the searched object.
     * Returns the most recent observation for each object found, with no specific ordering;
     */
    public String trackMatch(String type, String id){

        SiloOuterClass.TrackMatchResponse response;
        SiloOuterClass.ObjectType requestType;
        String observations = "";
        float[] coords;

        if (type.equals("person")){
            requestType = SiloOuterClass.ObjectType.person;
            response = stub.trackMatch(SiloOuterClass.TrackMatchRequest.newBuilder().setType(requestType).setId(id).build());
            return getTrackMatchString(response);
        } else if (type.equals("car")) {
            requestType = SiloOuterClass.ObjectType.car;
            response = stub.trackMatch(SiloOuterClass.TrackMatchRequest.newBuilder().setType(requestType).setId(id).build());
            return getTrackMatchString(response);
        }

        return "";
        //return getString(type, observations, response);

    }

    public String getTrackMatchString(SiloOuterClass.TrackMatchResponse response){
        String rest = "";

        for (SiloOuterClass.Observation o: response.getObsResList() ){
            rest += o.getType().toString() + ',' + o.getId() + ',' + o.getTimestamp() + ',' + o.getCam().getName() + ',' + o.getCam().getLatitude() + ',' + o.getCam().getLongitude() + "\n";

        }

        return rest;
    }

    /**
     * Arguments:
     * Receives the object type and the exact object identifier.
     * Returns a list of observations of the object, ordered from the most recent observation to the oldest.
     */
    public String trace(String type, String id){

        SiloOuterClass.TraceResponse response;
        SiloOuterClass.ObjectType requestType;
        String observations = "";
        float[] coords;

        if (type.equals("person")){
            requestType = SiloOuterClass.ObjectType.person;
            response = stub.trace(SiloOuterClass.TraceRequest.newBuilder().setType(requestType).setId(id).build());
            return getTraceString(response);
        } else if (type.equals("car")){
            requestType = SiloOuterClass.ObjectType.car;
            response = stub.trace(SiloOuterClass.TraceRequest.newBuilder().setType(requestType).setId(id).build());
            return getTraceString(response);
        }

        return "";
    }


    public String getTraceString(SiloOuterClass.TraceResponse response){
        String rest = "";

        for (SiloOuterClass.Observation o: response.getObsResList() ){
            rest += o.getType().toString() + ',' + o.getId() + ',' + o.getTimestamp() + ',' + o.getCam().getName() + ',' + o.getCam().getLatitude() + ',' + o.getCam().getLongitude() + "\n";
        }

        return rest;
    }

}
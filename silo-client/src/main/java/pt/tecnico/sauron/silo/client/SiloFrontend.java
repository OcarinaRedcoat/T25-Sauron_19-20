package pt.tecnico.sauron.silo.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.grpc.SiloGrpc;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass;

import java.util.List;

public class SiloFrontend {

    private  SiloGrpc.SiloBlockingStub stub;

    public SiloFrontend(String []args){

        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        final String target = host + ":" + port;

        // Channel is the abstraction to connect to a service endpoint.
        // Let us use plaintext communication because we do not have certificates.
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        // It is up to the client to determine whether to block the call.
        // Here we create a blocking stub, but an async stub, or an async stub with
        // Future are also available.
        stub = SiloGrpc.newBlockingStub(channel);


        // A Channel should be shutdown before stopping the process.
        channel.shutdownNow();
    }

    /**
     * Arguments: name (String), and 2 locations X and Y (floats)
     * Converts the location X and Y to latitude and longitude and create a camera in Silo-server
     * This method is to be called only in eye
     */
    public void camJoin(String name, float locationX, float locationY){
        //TODO check for errors
        String result = stub.camJoin(SiloOuterClass.CamJoinRequest.newBuilder().setLocal(name).setLatitude(locationX).setLongitude(locationY).build()).getResult();
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
    public boolean report(String type, String id, String camName){
        SiloOuterClass.ObjectType requestType;
        if (type.equals("person")){
            requestType = SiloOuterClass.ObjectType.PERSON;
        } else {
            requestType = SiloOuterClass.ObjectType.CAR;
        }
        SiloOuterClass.ReportResponse response = stub.report(SiloOuterClass.ReportRequest.newBuilder().setType(requestType).setId(id).setCamName(camName).build());

        return response.getError();
    }

    /**
     * Arguments: name (String) id (String)
     * Receives the type of object to find and the identifier of the object sought.
     * Returns the most recent observation of the searched object;
     */
    public String track(String type, String id){
        SiloOuterClass.ObjectType requestType;
        if (type.equals("person")){
            requestType = SiloOuterClass.ObjectType.PERSON;
        } else {
            requestType = SiloOuterClass.ObjectType.CAR;
        }
        SiloOuterClass.ObservationResponse response = stub.track(SiloOuterClass.TTTRequest.newBuilder().setType(requestType).setId(id).build());

        String camLocal = response.getCamName();

        float []coords = new float[2];

        coords = camInfo(camLocal);

        return type + ',' + response.getId() + ',' + response.getTimestamp().toString() + ',' + camLocal + ',' + coords[0] + ',' + coords[1];

    }

    /**
     * Arguments:
     * Allows you to locate an observed object with part of its identifier.
     * Receives the type of object to find and part of the identifier of the searched object.
     * Returns the most recent observation for each object found, with no specific ordering;
     */
    public String trackMatch(String type, String id){

        SiloOuterClass.ObjectType requestType;
        String observations = "";
        float[] coords;

        if (type.equals("person")){
            requestType = SiloOuterClass.ObjectType.PERSON;
        } else {
            requestType = SiloOuterClass.ObjectType.CAR;
        }
        //FIXME
        SiloOuterClass.ObservationListResponse response = stub.trackMatch(SiloOuterClass.TTTRequest.newBuilder().setType(requestType).setId(id).build());

        for(SiloOuterClass.ObservationResponse obList : response.getObservationlistList()) {
            String camLocal = obList.getCamName();
            coords = camInfo(camLocal);
            observations += type + ',' + obList.getId() + ',' + obList.getTimestamp().toString() + ',' + camLocal + ',' + coords[0] + ',' + coords[1] + "\n";

        }
        return observations;

    }

    /**
     * Arguments:
     * Receives the object type and the exact object identifier.
     * Returns a list of observations of the object, ordered from the most recent observation to the oldest.
     */
    public String trace(String type, String id){
        SiloOuterClass.ObjectType requestType;
        if (type.equals("person")){
            requestType = SiloOuterClass.ObjectType.PERSON;
        } else {
            requestType = SiloOuterClass.ObjectType.CAR;
        }
        //FIXME
        SiloOuterClass.ObservationResponse response = stub.track(SiloOuterClass.TTTRequest.newBuilder().setType(requestType).setId(id).build());

//        String camLocal = response.getCamName();

//        float []coords = new float[2];

//        coords = camInfo(camLocal);

//        return type + ',' + response.getId() + ',' + response.getTimestamp().toString() + ',' + camLocal + ',' + coords[0] + ',' + coords[1];
        return "";
    }
}
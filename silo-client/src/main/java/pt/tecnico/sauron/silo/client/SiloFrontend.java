package pt.tecnico.sauron.silo.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.grpc.SiloGrpc;
import pt.tecnico.sauron.silo.grpc.SiloOuterClass;

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
     *
     * Converts the location X and Y to latitude and longitude and create a camera in Silo-server
     * This method is to be called only in eye
     *
     */
    public void camJoin(String name, float locationX, float locationY){
        //TODO check for errors
        String result = stub.camJoin(SiloOuterClass.CamJoinRequest.newBuilder().setLocal(name).setLatitude(locationX).setLongitude(locationY).build()).getResult();
    }

    /**
     * Arguments: name (String)
     *
     * Asks the server for the coordinates of a camera that has the given name
     *
     * Returns the coordinates in a float array size 2
     */

    public float[] camInfo(String camName){

        //CamInfoResponse is a pair of floats
        SiloOuterClass.CamInfoResponse response = stub.camInfo(SiloOuterClass.CamInfoRequest.newBuilder().setLocal(camName).build());
        float []coords = new float[2];
        coords[0] = response.getLatitude(); coords[1] = response.getLongitude();
        return coords;
    }

}
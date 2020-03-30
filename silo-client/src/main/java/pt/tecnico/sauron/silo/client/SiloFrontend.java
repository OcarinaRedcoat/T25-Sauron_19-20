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


    public void createCamera(String name, float locationX, float locationY){

        String result = stub.camJoin(SiloOuterClass.CamJoinRequest.newBuilder().setLocal(name).setLatitude(locationX).setLongitude(locationY)).build();

    }




}

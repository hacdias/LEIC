package pt.tecnico.sauron.silo;

import pt.tecnico.sauron.silo.grpc.SauronGrpc;

public class SiloServiceImpl extends SauronGrpc.SauronImplBase{

    private Sauron sauron = new Sauron();
}
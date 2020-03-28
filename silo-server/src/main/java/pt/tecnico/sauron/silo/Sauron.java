package pt.tecnico.sauron.silo;

import java.util.ArrayList;
import java.util.List;

import pt.tecnico.sauron.silo.grpc.Silo.Coordinates;
import pt.tecnico.sauron.silo.grpc.Silo.Identifier;
import pt.tecnico.sauron.silo.grpc.Silo.Observation;
import pt.tecnico.sauron.silo.grpc.Silo.ObservationObject;
import pt.tecnico.sauron.silo.grpc.Silo.Camera;

public class Sauron {

    private List<Observation> observations = new ArrayList<Observation>();
    private List<Camera> cameras = new ArrayList<Camera>();

    public Coordinates getCamInfo (String name){
        return null;

    }

    public void addCamera(Camera camera){

    }

    public Observation track(ObservationObject type, Identifier identifier) {
        return null;
    }

    public Observation trackMatch(ObservationObject type, String partIdentifier) {
        return null;
    }

    public List<Observation> trace(ObservationObject type, Identifier identifier) {
        return null;
    }

}
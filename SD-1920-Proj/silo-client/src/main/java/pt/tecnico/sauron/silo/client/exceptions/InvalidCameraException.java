package pt.tecnico.sauron.silo.client.exceptions;

public class InvalidCameraException extends SauronClientException {
    private static final long serialVersionUID = 9200692142826563723L;

    public InvalidCameraException() {
    }

    public String toString(){
        return ("InvalidCameraException Occurred") ;
    }
}

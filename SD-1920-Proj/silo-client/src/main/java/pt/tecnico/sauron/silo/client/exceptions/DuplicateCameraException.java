package pt.tecnico.sauron.silo.client.exceptions;

public class DuplicateCameraException extends SauronClientException {
    private static final long serialVersionUID = 9200692141826563723L;

    public DuplicateCameraException() {
    }

    public String toString(){
        return ("DuplicateCameraException Occurred") ;
    }
}

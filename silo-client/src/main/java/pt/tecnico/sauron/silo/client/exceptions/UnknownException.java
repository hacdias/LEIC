package pt.tecnico.sauron.silo.client.exceptions;

public class UnknownException extends SauronClientException {
    private static final long serialVersionUID = 9200692141826563723L;

    public UnknownException() {
    }

    public String toString(){
        return ("UnknownException Occurred") ;
    }
}

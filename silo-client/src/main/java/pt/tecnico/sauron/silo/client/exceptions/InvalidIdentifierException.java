package pt.tecnico.sauron.silo.client.exceptions;

public class InvalidIdentifierException extends SauronClientException {
    private static final long serialVersionUID = 9200692143824563723L;

    public InvalidIdentifierException() {
    }

    public String toString(){
        return ("InvalidIdentifierException Occurred") ;
    }
}

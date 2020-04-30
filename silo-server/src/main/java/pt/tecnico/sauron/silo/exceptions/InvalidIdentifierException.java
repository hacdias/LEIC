package pt.tecnico.sauron.silo.exceptions;

public class InvalidIdentifierException extends Exception {
    private static final long serialVersionUID = 9200692101826163723L;
    private final String identifier;

    public InvalidIdentifierException(String id) {
        identifier = id;
    }

    public String toString(){
        return ("InvalidIdentifierException Occurred: " + identifier) ;
    }

    public String getIdentifier() {
        return identifier;
    }
}

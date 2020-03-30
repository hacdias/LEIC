package pt.tecnico.sauron.silo.client.exceptions;

abstract public class SauronClientException extends Exception {
  private static final long serialVersionUID = -150946883501722399L;

  public SauronClientException() {
  }

  public String toString(){
    return ("SauronClientException Occurred") ;
  }
}

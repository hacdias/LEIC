package sth.exceptions;

public class BadTypeException extends Exception {
  private static final long serialVersionUID = 201409301048L;
  private String _type;

  public BadTypeException(String type) {
    _type = type;
  }

  public String getType() {
    return _type;
  }
}

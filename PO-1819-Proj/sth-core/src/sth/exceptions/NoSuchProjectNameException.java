package sth.exceptions;

public class NoSuchProjectNameException extends Exception {
  private static final long serialVersionUID = 201809021324L;
  private String _name;

  public NoSuchProjectNameException(String name) {
    _name = name;
  }

  public String getName() {
    return _name;
  }
}

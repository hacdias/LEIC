package sth.exceptions;

public class DuplicateProjectNameException extends Exception {
  private static final long serialVersionUID = 201809021324L;
  private String _name;

  public DuplicateProjectNameException(String name) {
    _name = name;
  }

  public String getName() {
    return _name;
  }
}

package sth.exceptions;

/** Exception thrown when the requested person does not exist. */
public class NoSuchProjectNameException extends Exception {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 201809021324L;

  /** Person id. */
  private String _name;

  /**
   * @param name
   */
  public NoSuchProjectNameException(String name) {
    _name = name;
  }

  /** @return name */
  public String getName() {
    return _name;
  }

}

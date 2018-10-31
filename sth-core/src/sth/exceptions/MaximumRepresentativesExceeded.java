package sth.exceptions;

public class MaximumRepresentativesExceeded extends Exception {
  /** Serial number for serialization. */
  private static final long serialVersionUID = 201809021324L;

  private String _name;

  public MaximumRepresentativesExceeded(String course) {
    _name = course;
  }

  public String getStudent() {
    return _name;
  }
}

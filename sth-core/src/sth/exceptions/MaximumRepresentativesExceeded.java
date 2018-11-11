package sth.exceptions;

public class MaximumRepresentativesExceeded extends Exception {
  /** Serial number for serialization. */
  private static final long serialVersionUID = 201809021324L;

  private String _course;

  public MaximumRepresentativesExceeded(String course) {
    _course = course;
  }

  public String getCourse() {
    return _course;
  }
}

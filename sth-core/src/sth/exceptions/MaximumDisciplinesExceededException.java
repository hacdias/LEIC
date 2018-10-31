package sth.exceptions;

public class MaximumDisciplinesExceededException extends Exception {
  /** Serial number for serialization. */
  private static final long serialVersionUID = 201809021324L;

  private String _student;

  public MaximumDisciplinesExceededException(String student) {
    _student = student;
  }

  public String getStudent() {
    return _student;
  }
}

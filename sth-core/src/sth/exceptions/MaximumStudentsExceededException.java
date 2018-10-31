package sth.exceptions;

public class MaximumStudentsExceededException extends Exception {
  /** Serial number for serialization. */
  private static final long serialVersionUID = 201809021324L;

  private String _discipline;
  private int _maximumStudents;

  public MaximumStudentsExceededException(String discipline, int maximumStudents) {
    _discipline = discipline;
    _maximumStudents = maximumStudents;
  }

  public String getDiscipline() {
    return _discipline;
  }

  public int getMaximumStudents() {
    return _maximumStudents;
  }
}

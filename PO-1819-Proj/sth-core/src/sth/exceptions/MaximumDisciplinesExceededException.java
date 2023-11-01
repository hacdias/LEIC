package sth.exceptions;

public class MaximumDisciplinesExceededException extends Exception {
  private static final long serialVersionUID = 201809021324L;

  private int _id;

  public MaximumDisciplinesExceededException(int id) {
    _id = id;
  }

  public int getId() {
    return _id;
  }
}

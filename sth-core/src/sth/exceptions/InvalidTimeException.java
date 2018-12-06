package sth.exceptions;

public class InvalidTimeException extends Exception {
  private double _time;
  private static final long serialVersionUID = 201708301010L;

  public InvalidTimeException(double time) {
    _time = time;
  }

  public double getTime() {
    return _time;
  }
}

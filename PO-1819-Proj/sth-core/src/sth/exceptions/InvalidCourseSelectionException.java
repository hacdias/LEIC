package sth.exceptions;

/**
 * Class for representing a read error.
 */
public class InvalidCourseSelectionException extends Exception {

  private String _courseName;

  /** Serial number for serialization. */
  private static final long serialVersionUID = 201708301010L;

  /**
   * @param courseName
   */
  public InvalidCourseSelectionException(String courseName) {
    _courseName = courseName;
  }

  /**
   * @return the _courseName
   */
  public String getCourseName() {
    return _courseName;
  }

}

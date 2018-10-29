package sth;

import java.io.Serializable;

public class Submission implements Serializable {
  private static final long serialVersionUID = 201810051538L;

  private Student _student;
  private String _value;

  public Submission(Student student, String value) {
    _student = student;
    _value = value;
  }

  public Student getStudent() {
    return _student;
  }

  public String getValue() {
    return _value;
  }

}
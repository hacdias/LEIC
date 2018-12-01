package sth;

import java.io.Serializable;

public class Submission implements Serializable, Comparable<Submission> {
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

  @Override
  public String toString() {
    return "* " + _student.getId() + " - " + _value;
  }

  @Override
  public int compareTo(Submission s) {
    return getStudent().compareTo(s.getStudent());
  }
}
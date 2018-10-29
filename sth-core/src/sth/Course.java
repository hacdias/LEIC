package sth;

import java.io.Serializable;
import java.lang.Comparable;
import java.util.HashMap;
import java.util.ArrayList;

public class Course implements Serializable, Comparable<Course> {
  private String _name;
  private final int MAX_REPRESENTATIVES = 7;
  private HashMap<Integer, Student> _students;
  private ArrayList<Student> _representatives;
  private ArrayList<Discipline> _disciplines;

  Course (String name) {
    _name = name;
  }

  /**
   * @return the _name
   */
  public String getName() {
    return _name;
  }

  @Override
  public int compareTo(Course o) {
    return _name.compareTo(o.getName());
  }
}

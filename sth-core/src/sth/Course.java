package sth;

import java.io.Serializable;
import java.lang.Comparable;
import java.util.HashMap;
import java.util.ArrayList;

public class Course implements Serializable, Comparable<Course> {
  private static final long serialVersionUID = 201810051538L;

  private String _name;
  private final int MAX_REPRESENTATIVES = 7;
  private HashMap<Integer, Student> _students;
  private ArrayList<Student> _representatives;
  private ArrayList<Discipline> _disciplines;

  Course (String name) {
    _name = name;
    _students = new HashMap<Integer, Student>();
    _representatives = new ArrayList<Student>();
    _disciplines = new ArrayList<Discipline>();
  }

  /**
   * @return the _name
   */
  public String getName() {
    return _name;
  }

  public boolean addDiscipline(Discipline d) {
    if (_disciplines.contains(d)) return false;
    _disciplines.add(d);
    return true;
  }
  public boolean removeDiscipline(Discipline d) {
    return _disciplines.remove(d);
  }

  public boolean addStudent(Student s) {
    if (_students.containsKey(s.getId())) return false;
    _students.put(s.getId(), s);
    return true;
  }
  public boolean removeStudent(Student s) {
    _students.remove(s.getId(), s);
    return true;
  }

  public boolean addRepresentative(Student r) {
    if (_representatives.contains(r) || _representatives.size() >= MAX_REPRESENTATIVES)
      return false;
    
    _representatives.add(r);
    return true;
  }
  public boolean removeRepresentative(Student r) {
    return _representatives.remove(r);
  }

  @Override
  public int compareTo(Course o) {
    return _name.compareTo(o.getName());
  }
}

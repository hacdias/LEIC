package sth;

import sth.exceptions.MaximumRepresentativesExceeded;
import java.io.Serializable;
import java.lang.Comparable;
import java.util.HashMap;
import java.util.HashSet;

public class Course implements Serializable, Comparable<Course> {
  private static final long serialVersionUID = 201810051538L;

  private String _name;
  private final int MAX_REPRESENTATIVES = 7;
  private HashMap<String, Discipline> _disciplines;
  private HashSet<Student> _representatives;

  Course (String name) {
    _name = name;
    _disciplines = new HashMap<String, Discipline>();
    _representatives = new HashSet<Student>();
  }

  public String getName() {
    return _name;
  }

  public Discipline getDiscipline(String name) {
    return _disciplines.get(name);
  }

  public void addDiscipline(Discipline d) {
    _disciplines.put(d.getName(), d);
  }

  public void removeDiscipline(Discipline d) {
    _disciplines.remove(d.getName());
  }

  public void addRepresentative(Student r) throws MaximumRepresentativesExceeded {
    if (_representatives.size() >= MAX_REPRESENTATIVES)
      throw new MaximumRepresentativesExceeded(_name);
    
    _representatives.add(r);
  }

  public boolean isRepresentative(Student r) {
    return _representatives.contains(r);
  }

  public void removeRepresentative(Student r) {
    _representatives.remove(r);
  }

  @Override
  public int compareTo(Course o) {
    return _name.compareTo(o.getName());
  }
}

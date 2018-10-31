package sth;

import sth.exceptions.OneProfessorNeededException;
import sth.exceptions.MaximumStudentsExceededException;

import java.io.Serializable;
import java.lang.Comparable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HashMap;

public class Discipline implements Serializable, Comparable<Discipline> {
  private static final long serialVersionUID = 201810051538L;

  private String _name;
  private int _maxStudents = 30;
  private Course _course;
  private HashMap<Integer, Professor> _professors;
  private HashMap<Integer, Student> _students;
  private HashSet<Project> _projects;

  public static final Comparator<Discipline> COURSE_COMPARATOR = new CourseComparator();

  public Discipline(String name, Course c) {
    _name = name;
    _course = c;
    _professors = new HashMap<Integer, Professor>();
    _projects = new HashSet<Project>();
    _students = new HashMap<Integer, Student>();
  }

  public String getName() {
    return _name;
  }

  public Course getCourse() {
    return _course;
  }

  public void setMaxStudents(int max) {
    _maxStudents = max;
  }

  public void addProfessor(Professor p) {
    _professors.put(p.getId(), p);
  }

  public void removeProfessor(Professor p) throws OneProfessorNeededException {
    if (_professors.size() <= 1)
      throw new OneProfessorNeededException(_name, p.getName());

    _professors.remove(p.getId(), p);
  }

  public void addProject(Project p) {
    _projects.add(p);
  }

  public void removeProject(Project p) {
    _projects.remove(p);
  }

  public void addStudent(Student s) throws MaximumStudentsExceededException {
    if (_students.size() >= _maxStudents)
      throw new MaximumStudentsExceededException(_name, _maxStudents);

    _students.put(s.getId(), s);
  }

  public void removeStudent(Student s) {
    _students.remove(s.getId(), s);
  }

  @Override
  public int compareTo(Discipline d) {
    return _name.compareTo(d.getName());
  }

  @Override
  public String toString() {
    return "* " + _course.getName() + " - " + _name;
  }

  private static class CourseComparator implements Comparator<Discipline> {
    @Override
    public int compare(Discipline d1, Discipline d2) {
      int v = d1.getCourse().compareTo(d2.getCourse());
      return v == 0 ? d1.compareTo(d2) : v;
    }
  }
}
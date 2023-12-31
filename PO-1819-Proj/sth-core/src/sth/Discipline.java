package sth;

import sth.exceptions.OneProfessorNeededException;
import sth.exceptions.DuplicateProjectNameException;
import sth.exceptions.MaximumStudentsExceededException;
import sth.exceptions.NoSuchProjectNameException;

import java.io.Serializable;
import java.lang.Comparable;
import java.util.Comparator;
import java.util.Locale;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;

public class Discipline implements Serializable, Comparable<Discipline> {
  private static final long serialVersionUID = 201810051538L;

  private String _name;
  private int _maxStudents = 30;
  private Course _course;
  private TreeMap<Integer, Professor> _professors;
  private TreeMap<Integer, Student> _students;
  private TreeMap<String, Project> _projects;
  private Comparator<String> _stringComparator;

  public static final Comparator<Discipline> COURSE_COMPARATOR = new CourseComparator();

  public Discipline(String name, Course c) {
    _name = name;
    _course = c;
    _professors = new TreeMap<Integer, Professor>();
    _projects = new TreeMap<String, Project>();
    _students = new TreeMap<Integer, Student>();
    _stringComparator = new StringComparator();
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

  public void addProject(Project p) throws DuplicateProjectNameException {
    if (_projects.get(p.getName()) != null)
      throw new DuplicateProjectNameException(p.getName());

    _projects.put(p.getName(), p); 
  }

  public void removeProject(Project p) {
    _projects.remove(p);
  }

  public Project getProject(String name) throws NoSuchProjectNameException {
    Project p = _projects.get(name);

    if (p == null)
      throw new NoSuchProjectNameException(name);

    return p;
  }

  public void addStudent(Student s) throws MaximumStudentsExceededException {
    if (_students.get(s.getId()) != null)
      return;

    if (_students.size() >= _maxStudents)
      throw new MaximumStudentsExceededException(_name, _maxStudents);

    _students.put(s.getId(), s);
  }

  public String getStudents() {
    String s = "";
    UserDescription u = new UserDescription();

    for (Student student : _students.values()) {
      s += student.accept(u) + "\n";
    }

    return s.trim();
  }

  public void removeStudent(Student s) {
    _students.remove(s.getId());
  }

  public void subscribeToSurvey(Survey survey) {
    for (Student s : _students.values())
      survey.attach(s);
    for (Professor p : _professors.values())
      survey.attach(p);

    for (Person p : _course.getRepresentatives().values())
      if (_students.get(p.getId()) == null)
        survey.attach(p);
  }

  public Collection<Project> getProjects() {
    return Collections.unmodifiableCollection(_projects.values());
  }

  @Override
  public int compareTo(Discipline d) {
    return _stringComparator.compare(_name, d.getName());
  }

  @Override
  public String toString() {
    return "* " + _course.getName() + " - " + _name;
  }

  private static class CourseComparator implements Serializable, Comparator<Discipline> {
    private static final long serialVersionUID = 201810051538L;

    @Override
    public int compare(Discipline d1, Discipline d2) {
      int v = d1.getCourse().compareTo(d2.getCourse());
      return v == 0 ? d1.compareTo(d2) : v;
    }
  }
}
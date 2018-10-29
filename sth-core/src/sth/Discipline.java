package sth;

import java.io.Serializable;
import java.lang.Comparable;
import java.util.ArrayList;
import java.util.HashMap;

public class Discipline implements Serializable, Comparable<Discipline> {
  private static final long serialVersionUID = 201810051538L;

  private String _name;
  private HashMap<Integer, Professor> _professors;
  private ArrayList<Project> _projects;
  private int _maxStudents;
  private HashMap<Integer, Students> _students;

  public Discipline(String name, int maxStudents, Professor professor) {
    _name = name;
    _maxStudents = maxStudents;
    _professors = new HashMap<Integer, Professor>();
    _projects = new ArrayList<Project>();
    _students = new HashMap<Integer, Student>();
    _professors.put(professor.getId(), professor);
  }

  public boolean addProfessor(Professor p) {
    if (_professors.containsKey(p.getId())) return false;
    _professors.put(p.getId(), p);
    return true;
  }
  public boolean removeProfessor(Professor p) {
    if (_professors.size() <= 1) return false;
    _disciplines.remove(p.getId(), p);
    return true;
  }

  public boolean addProject(Project p) {
    if (_projects.contains(p)) return false;
    _projects.add(p);
    return true;
  }
  public boolean removeProject(Project p) {
    return _projects.remove(p);
  }

  public boolean addStudent(Student s) {
    if (_students.size() >= _maxStudents) return false;
    _students.put(s.getId(), s);
    return true;
  }
  public boolean removeStudent(Student s) {
    _students.remove(s.getId(), s);
    return true;
  }

  @Override
  public int compareTo(Discipline d) {
    return _name.compareTo(d.getName());
  }
}
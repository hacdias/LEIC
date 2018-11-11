package sth;

import sth.exceptions.MaximumDisciplinesExceededException;
import java.io.Serializable;
import java.util.TreeSet;

public class Student extends Person {
  private static final long serialVersionUID = 201810051538L;

  private final int MAX_DISCIPLINES = 6;
  private Course _course;
  private TreeSet<Discipline> _disciplines;

  Student(int id,  String phoneNumber, String name) {
    super(id, phoneNumber, name);
    _disciplines = new TreeSet<Discipline>();
  }

  public void setCourse(Course c) {
    _course = c;
  }

  public Course getCourse() {
    return _course;
  }

  public void addDiscipline(Discipline d) throws MaximumDisciplinesExceededException {
    // It shouldn't exceed though.
    if (_disciplines.size() >= MAX_DISCIPLINES)
      throw new MaximumDisciplinesExceededException(getId());

    _disciplines.add(d);
  }

  public void removeDiscipline(Discipline d) {
    _disciplines.remove(d);
  }

  @Override
  public String toString() {
    String me = _course.isRepresentative(this) ? "DELEGADO" : "ALUNO";
    me += "|" + super.toString();

    for (Discipline d : _disciplines) {
      me += "\n" + d.toString();
    }

    return me;
  }
}

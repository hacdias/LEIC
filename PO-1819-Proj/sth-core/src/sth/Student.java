package sth;

import sth.exceptions.MaximumDisciplinesExceededException;
import sth.exceptions.NoSuchDisciplineNameException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;

public class Student extends Person implements WithDisciplines {
  private static final long serialVersionUID = 201810051538L;

  private final int MAX_DISCIPLINES = 6;
  private Course _course;
  private TreeMap<String, Discipline> _disciplines;

  Student(int id, String phoneNumber, String name) {
    super(id, phoneNumber, name);
    _disciplines = new TreeMap<String, Discipline>(new StringComparator());
  }

  public void setCourse(Course c) {
    _course = c;
  }

  public Course getCourse() {
    return _course;
  }

  public void addDiscipline(Discipline d) throws MaximumDisciplinesExceededException {
    if (_disciplines.get(d.getName()) != null)
      return;

    if (_disciplines.size() >= MAX_DISCIPLINES)
      throw new MaximumDisciplinesExceededException(getId());

    _disciplines.put(d.getName(), d);
  }

  public Discipline getDiscipline(String name) throws NoSuchDisciplineNameException {
    Discipline d = _disciplines.get(name);
    if (d == null) {
      throw new NoSuchDisciplineNameException(name);
    }

    return d;
  }

  public void removeDiscipline(Discipline d) {
    _disciplines.remove(d.getName());
  }

  public Collection<Discipline> getDisciplines() {
    return Collections.unmodifiableCollection(_disciplines.values());
  }

  public String accept(UserDescription u) {
    return _course.isRepresentative(this) ? u.descRepresentative(this) : u.descStudent(this);
  }
}

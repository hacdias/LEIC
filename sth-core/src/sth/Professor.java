package sth;

import java.io.Serializable;
import java.util.TreeSet;

import sth.exceptions.NoSuchDisciplineNameException;

public class Professor extends Person {
  private static final long serialVersionUID = 201810051538L;

  private TreeSet<Discipline> _disciplines;

  Professor(int id, String phoneNumber, String name) {
    super(id, phoneNumber, name);
    _disciplines = new TreeSet<Discipline>(Discipline.COURSE_COMPARATOR);
  }

  public boolean addDiscipline(Discipline d) {
    _disciplines.add(d);
    return true;
  }

  public boolean removeDiscipline(Discipline d) {
    return _disciplines.remove(d);
  }

  public Discipline teachesDiscipline(String name) throws NoSuchDisciplineNameException {
    for (Discipline d : _disciplines) {
      if (name.equals(d.getName()))
        return d;
    }

    throw new NoSuchDisciplineNameException(name);
  }

  @Override
  public String toString() {
    String me = "DOCENTE|" + super.toString();

    for (Discipline d : _disciplines) {
      me += "\n" + d.toString();
    }

    return me;
  }
}

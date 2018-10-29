package sth;

import java.io.Serializable;
import java.util.HashSet;

public class Professor extends Person implements Serializable {
  private static final long serialVersionUID = 201810051538L;
  
  private HashSet<Discipline> _disciplines;

  Professor(String name, String phoneNumber, int id) {
    super(name, phoneNumber, id);
    _disciplines = new HashSet<Discipline>();
  }

  public boolean addDiscipline(Discipline d) {
    _disciplines.add(d);
    return true;
  }

  public boolean removeDiscipline(Discipline d) {
    return _disciplines.remove(d);
  }
}

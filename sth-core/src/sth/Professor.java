package sth;

import java.util.HashSet;

public class Professor extends Person {
  private HashSet<Discipline> _disciplines;

  Professor(String name, String phoneNumber, int id) {
    super(name, phoneNumber, id);
    _disciplines = new HashSet<Discipline>();
  }

  public void addDiscipline(Discipline d) {
    _disciplines.add(d);
  }

  public void removeDiscipline(Discipline d) {
    _disciplines.remove(d);
  }
}

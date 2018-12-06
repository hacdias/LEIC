package sth;
import sth.exceptions.MaximumRepresentativesExceeded;
import sth.exceptions.NoSuchDisciplineNameException;

import java.io.Serializable;
import java.lang.Comparable;
import java.util.Comparator;
import java.util.Locale;
import java.util.HashMap;

public class Course implements Serializable, Comparable<Course> {
  private static final long serialVersionUID = 201810051538L;

  private String _name;
  private final int MAX_REPRESENTATIVES = 7;
  private HashMap<String, Discipline> _disciplines;
  private HashMap<Integer, Person> _representatives;
  private Comparator<String> _stringComparator;

  Course(String name) {
    _name = name;
    _disciplines = new HashMap<String, Discipline>();
    _representatives = new HashMap<Integer, Person>();
    _stringComparator = new StringComparator();
  }

  public String getName() {
    return _name;
  }

  public Discipline getDiscipline(String name) throws NoSuchDisciplineNameException {
    Discipline d = _disciplines.get(name);
    if (d == null) {
      throw new NoSuchDisciplineNameException(name);
    }

    return d;
  }

  public void addDiscipline(Discipline d) {
    _disciplines.put(d.getName(), d);
  }

  public void removeDiscipline(Discipline d) {
    _disciplines.remove(d.getName());
  }

  public void addRepresentative(Person r) throws MaximumRepresentativesExceeded {
    if (_representatives.get(r.getId()) == null) {
      if (_representatives.size() >= MAX_REPRESENTATIVES)
        throw new MaximumRepresentativesExceeded(_name);

      _representatives.put(r.getId(), r);
    }
  }

  public boolean isRepresentative(Person r) {
    return _representatives.get(r.getId()) != null;
  }

  public HashMap<Integer, Person> getRepresentatives() {
    return _representatives;
  }

  public void removeRepresentative(Person r) {
    _representatives.remove(r.getId());
  }

  @Override
  public int compareTo(Course o) {
    return _stringComparator.compare(_name, o.getName());
  }
}

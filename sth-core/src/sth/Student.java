package sth;

import java.util.HashSet;

public class Student extends Person {
  private final int MAX_DISCIPLINES = 6;
  private Course _course;
  private HashSet<Discipline> _disciplines;

  Student(Course course, String name, String phoneNumber, int id) {
    super(name, phoneNumber, id);
    _course = course;
    _disciplines = new HashSet<Discipline>();
  }

  /**
   * @param d is the discipline.
   * @return false if the limit of disciplines was exceeded and true otherwise.
   */
  public boolean enrollDiscipline(Discipline d) {
    // It shouldn't exceed though.
    if (_disciplines.size() >= MAX_DISCIPLINES) {
      return false;
    }

    _disciplines.add(d);
    return true;
  }

  public void unenrollDiscipline(Discipline d) {
    _disciplines.remove(d);
  }
}

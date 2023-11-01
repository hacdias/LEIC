package sth.exceptions;

public class OneProfessorNeededException extends Exception {
  private static final long serialVersionUID = 201809021324L;

  private String _discipline;
  private String _professor;

  public OneProfessorNeededException(String discipline, String professor) {
    _discipline = discipline;
    _professor = professor;
  }

  public String getDiscipline() {
    return _discipline;
  }

  public String getProfessor() {
    return _professor;
  }
}

package sth.exceptions;

public class NoSurveyProjectException extends Exception {
  private static final long serialVersionUID = 201809021324L;
  private String _discipline;
  private String _project;

  public NoSurveyProjectException(String project) {
    _project = project;
  }

  public String getName() {
      return _project;
  }
}
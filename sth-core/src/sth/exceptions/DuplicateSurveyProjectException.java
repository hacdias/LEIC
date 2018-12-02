package sth.exceptions;

public class DuplicateSurveyProjectException extends Exception {
  private static final long serialVersionUID = 201809021324L;
  private String _project;

  public DuplicateSurveyProjectException(String project) {
    _project = project;
  }

  public String getName() {
    return _project;
  }
}
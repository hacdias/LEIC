package sth;

import java.io.Serializable;

import sth.exceptions.NoSurveyProjectException;
import sth.exceptions.NonEmptySurveyProjectException;
import sth.exceptions.SurveyFinishedProjectException;
import sth.exceptions.OpeningSurveyProjectException;
import sth.exceptions.ClosingSurveyProjectException;
import sth.exceptions.FinishingSurveyProjectException;

public abstract class SurveyState implements Serializable {
  private static final long serialVersionUID = 201810051538L;

  private Survey _survey;

  public SurveyState(Survey survey) {
    _survey = survey;
  }

  public abstract void cancel() throws NonEmptySurveyProjectException, SurveyFinishedProjectException;

  public abstract void open() throws OpeningSurveyProjectException;

  public abstract void close() throws ClosingSurveyProjectException;

  public abstract void finalize() throws FinishingSurveyProjectException;

  public abstract String printInfo(SurveyPrint printer);

  public abstract void submitEntry(Student s, SurveyEntry e) throws NoSurveyProjectException;

  public Survey getSurvey() {
    return _survey;
  }

  public void setState(SurveyState new_state) {
    getSurvey().setState(new_state);
  }

}
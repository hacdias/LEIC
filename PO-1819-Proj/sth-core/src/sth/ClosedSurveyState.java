package sth;

import java.io.Serializable;

import sth.exceptions.NoSurveyProjectException;

public class ClosedSurveyState extends SurveyState {
  private static final long serialVersionUID = 201810051538L;

  public ClosedSurveyState(Survey survey) {
    super(survey);
  }

  public void cancel() {
    setState(new OpenSurveyState(getSurvey()));
  }

  public void open() {
    setState(new OpenSurveyState(getSurvey()));
  }

  public void close() {
  }

  public void finalize() {
    setState(new FinalizedSurveyState(getSurvey()));
  }

  public String printInfo(SurveyPrint printer) {
    return printer.printSurveyClosed(getSurvey());
  }

  public void submitEntry(Student s, SurveyEntry e) throws NoSurveyProjectException {
    Survey survey = getSurvey();
    Project p = survey.getProject();

    throw new NoSurveyProjectException(p.getName());
  }
}
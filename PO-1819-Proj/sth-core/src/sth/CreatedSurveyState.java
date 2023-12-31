package sth;

import java.io.Serializable;

import sth.exceptions.NoSurveyProjectException;
import sth.exceptions.OpeningSurveyProjectException;
import sth.exceptions.ClosingSurveyProjectException;
import sth.exceptions.FinishingSurveyProjectException;

public class CreatedSurveyState extends SurveyState {
  private static final long serialVersionUID = 201810051538L;

  public CreatedSurveyState(Survey survey) {
    super(survey);
  }

  public void cancel() {
    Project p = getSurvey().getProject();
    p.removeSurvey();
  }

  public void open() throws OpeningSurveyProjectException {
    Project p = getSurvey().getProject();
    if (p.isOpen())
      throw new OpeningSurveyProjectException();

    setState(new OpenSurveyState(getSurvey()));
  }

  public void close() throws ClosingSurveyProjectException {
    throw new ClosingSurveyProjectException();
  }

  public void finalize() throws FinishingSurveyProjectException {
    throw new FinishingSurveyProjectException();
  }

  public String printInfo(SurveyPrint printer) {
    return printer.printSurveyCreated(getSurvey());
  }

  public void submitEntry(Student s, SurveyEntry e) throws NoSurveyProjectException {
    Survey survey = getSurvey();
    Project p = survey.getProject();

    throw new NoSurveyProjectException(p.getName());
  }
}
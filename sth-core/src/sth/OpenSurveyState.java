package sth;

import java.io.Serializable;

import sth.exceptions.NonEmptySurveyProjectException;
import sth.exceptions.FinishingSurveyProjectException;

public class OpenSurveyState extends SurveyState {
  private static final long serialVersionUID = 201810051538L;

  public OpenSurveyState(Survey survey) {
    super(survey);
    Project p = survey.getProject();
    Discipline d = p.getDiscipline();
    survey.notify("Pode preencher inquérito do projecto " + p.getName() + " da disciplina " + d.getName());
  }

  public void cancel() throws NonEmptySurveyProjectException {
    if (getSurvey().getNumberEntries() != 0)
      throw new NonEmptySurveyProjectException();

    Project p = getSurvey().getProject();
    p.removeSurvey();
  }

  public void open() {
  }

  public void close() {
    setState(new ClosedSurveyState(getSurvey()));
  }

  public void finalize() throws FinishingSurveyProjectException {
    throw new FinishingSurveyProjectException();
  }

  public String printInfo(SurveyPrint printer) {
    return printer.printSurveyOpen(getSurvey());
  }
}
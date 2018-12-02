package sth;
import java.io.Serializable;

import sth.exceptions.SurveyFinishedProjectException;
import sth.exceptions.OpeningSurveyProjectException;
import sth.exceptions.ClosingSurveyProjectException;

public class FinalizedSurveyState extends SurveyState {
  private static final long serialVersionUID = 201810051538L;

  public FinalizedSurveyState(Survey survey) {
    super(survey);
    Project p = survey.getProject();
    Discipline d = p.getDiscipline();
    survey.notify("Finalizado o inquérito do projecto " + p.getName() + " da disciplina " + d.getName());
  }
      
  public void cancel() throws SurveyFinishedProjectException {
    throw new SurveyFinishedProjectException(); 
  }
  
  public void open() throws OpeningSurveyProjectException {
    throw new OpeningSurveyProjectException();
  }

  public void close() throws ClosingSurveyProjectException {
    throw new ClosingSurveyProjectException();
  }

  public void finalize() {}

  public String printInfo(SurveyPrint printer) {
    return printer.printSurveyFinalized(getSurvey());
  }
}
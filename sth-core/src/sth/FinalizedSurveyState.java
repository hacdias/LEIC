package sth;
import java.io.Serializable;

import sth.exceptions.SurveyFinishedProjectException;
import sth.exceptions.OpeningSurveyProjectException;
import sth.exceptions.ClosingSurveyProjectException;

public class FinalizedSurveyState extends SurveyState {
  private static final long serialVersionUID = 201810051538L;

  public FinalizedSurveyState(Survey survey) {
    super(survey);
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
}
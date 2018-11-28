package sth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import sth.exceptions.NonEmptySurveyProjectException;
import sth.exceptions.SurveyFinishedProjectException;
import sth.exceptions.OpeningSurveyProjectException;
import sth.exceptions.ClosingSurveyProjectException;
import sth.exceptions.FinishingSurveyProjectException;

public class Survey implements Serializable {
  private static final long serialVersionUID = 201810051538L;

  private Project _project;
  private ArrayList<SurveyEntry> _entries;
  private SurveyState _state;
  private HashMap<Integer, Student> _students;

  public Survey(Project project) {
    _project = project;
    _entries = new ArrayList<SurveyEntry>();
    _state = new CreatedSurveyState(this);
    _students = new HashMap<Integer, Student>();
  }

  public void cancel() throws NonEmptySurveyProjectException, SurveyFinishedProjectException { _state.cancel(); }
  public void open() throws OpeningSurveyProjectException { _state.open(); }
  public void close() throws ClosingSurveyProjectException { _state.close(); }
  public void finalize() throws FinishingSurveyProjectException { _state.finalize(); }

  public void setState(SurveyState new_state) {
    _state = new_state;
  }

  public void submitEntry() {
    // TODO: Intrepertation of States
  }

  public Project getProject() {
    return _project;
  }

  public int getNumberEntries() {
    return _entries.size();
  }
}
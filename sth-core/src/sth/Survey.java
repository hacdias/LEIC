package sth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import sth.exceptions.NonEmptySurveyProjectException;
import sth.exceptions.SurveyFinishedProjectException;
import sth.exceptions.OpeningSurveyProjectException;
import sth.exceptions.ClosingSurveyProjectException;
import sth.exceptions.FinishingSurveyProjectException;

public class Survey implements Serializable, Observable {
  private static final long serialVersionUID = 201810051538L;

  private Project _project;
  // ASK: Is it bad to use ArrayList in this case?
  private ArrayList<Observer> _observers;
  private ArrayList<SurveyEntry> _entries;
  private SurveyState _state;
  private HashMap<Integer, Student> _students;

  public Survey(Project project) {
    _project = project;
    _entries = new ArrayList<SurveyEntry>();
    _state = new CreatedSurveyState(this);
    _students = new HashMap<Integer, Student>();
    _observers = new ArrayList<Observer>();
  }

  public void cancel() throws NonEmptySurveyProjectException, SurveyFinishedProjectException { _state.cancel(); }
  public void open() throws OpeningSurveyProjectException { _state.open(); }
  public void close() throws ClosingSurveyProjectException { _state.close(); }
  public void finalize() throws FinishingSurveyProjectException { _state.finalize(); }
  public String printInfo(SurveyPrint printer) { return _state.printInfo(printer); }

  public void setState(SurveyState new_state) {
    _state = new_state;
  }

  public void submitEntry(Student s, SurveyEntry entry) {
    // ASK: Do we throw an exception if student already answered survey
    // TODO: Only submit entry if State of Survey is open
    if (_students.containsKey(s.getId())) {
      // TODO: Throw exception
    } else {
      _students.put(s.getId(), s);
      _entries.add(entry);
    }
  }

  public Project getProject() {
    return _project;
  }

  public int getNumberEntries() {
    return _entries.size();
  }

  public double getAverageTime() {
    double average = 0;
    for (SurveyEntry e : _entries)
      average += e.getSpentHours();
    
    return average/getNumberEntries();
  }

  public double getMinimumTime() {
    double minimum = _entries.get(0).getSpentHours();
    for (SurveyEntry e : _entries) {
      // TODO: Maybe not check first again?
      if (e.getSpentHours() < minimum)
        minimum = e.getSpentHours();
    }

    return minimum;
  }

  public double getMaximumTime() {
    double maximum = _entries.get(0).getSpentHours();
    for (SurveyEntry e : _entries) {
      // TODO: Maybe not check first again?
      if (e.getSpentHours() > maximum)
        maximum = e.getSpentHours();
    }

    return maximum;
  }

  public void attach(Observer o) {
    _observers.add(o);
  }

  public void detach(Observer p) {
    // TODO:
  }

  public void notify(String s) {
    for (Observer o : _observers)
      o.update(s);
  }
}
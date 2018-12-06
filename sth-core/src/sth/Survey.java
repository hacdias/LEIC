package sth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import sth.exceptions.NoSurveyProjectException;
import sth.exceptions.NonEmptySurveyProjectException;
import sth.exceptions.SurveyFinishedProjectException;
import sth.exceptions.OpeningSurveyProjectException;
import sth.exceptions.ClosingSurveyProjectException;
import sth.exceptions.FinishingSurveyProjectException;

public class Survey implements Serializable, Observable {
  private static final long serialVersionUID = 201810051538L;

  private Project _project;
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

  public void cancel() throws NonEmptySurveyProjectException, SurveyFinishedProjectException {
    _state.cancel();
  }

  public void open() throws OpeningSurveyProjectException {
    _state.open();
  }

  public void close() throws ClosingSurveyProjectException {
    _state.close();
  }

  public void finalize() throws FinishingSurveyProjectException {
    _state.finalize();
  }

  public String printInfo(SurveyPrint printer) {
    return _state.printInfo(printer);
  }

  public void submitEntry(Student s, SurveyEntry entry) throws NoSurveyProjectException {
    _state.submitEntry(s, entry);
  }

  public void setState(SurveyState new_state) {
    _state = new_state;
  }

  public Project getProject() {
    return _project;
  }

  public int getNumberEntries() {
    return _entries.size();
  }

  public boolean studentAlreadyAnswered(Student s) {
    return _students.containsKey(s.getId());
  }

  public void addEntry(Student s, SurveyEntry e) {
    _students.put(s.getId(), s);
    _entries.add(e);
  }

  public SurveyStats getStats() {
    if (_entries.size() == 0)
      return new SurveyStats(0, 0, 0);

    double avg = 0;
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;

    for (SurveyEntry e : _entries) {
      avg += e.getSpentHours();
      min = Math.min(min, e.getSpentHours());
      max = Math.max(max, e.getSpentHours());
    }

    avg /= _entries.size();
    return new SurveyStats(avg, min, max);
  }

  public void attach(Observer o) {
    _observers.add(o);
  }

  public void detach(Observer p) {
    _observers.remove(p);
  }

  public void notify(String s) {
    for (Observer o : _observers)
      o.update(s);
  }
}
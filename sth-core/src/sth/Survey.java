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
  private double _maximumTime;
  private double _minimumTime;
  private double _averageTime;

  public Survey(Project project) {
    _project = project;
    _entries = new ArrayList<SurveyEntry>();
    _state = new CreatedSurveyState(this);
    _students = new HashMap<Integer, Student>();
    _observers = new ArrayList<Observer>();
    _maximumTime = 0;
    _minimumTime = Double.MAX_VALUE;
    _averageTime = 0;
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
    _maximumTime = Math.max(entry.getSpentHours(), _maximumTime);
    _minimumTime = Math.min(entry.getSpentHours(), _minimumTime);
    _averageTime = (_averageTime * getNumberEntries() + entry.getSpentHours()) / (getNumberEntries() + 1);
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

  public double getAverageTime() {
    return _averageTime;
  }

  public double getMinimumTime() {
    return _minimumTime;
  }

  public double getMaximumTime() {
    return _maximumTime;
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
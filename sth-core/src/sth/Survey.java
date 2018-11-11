package sth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Survey implements Serializable {
  private static final long serialVersionUID = 201810051538L;

  private ArrayList<SurveyEntry> _entries;
  private SurveyState _state;
  private HashMap<Integer, Student> _students;

  public Survey() {
    _entries = new ArrayList<SurveyEntry>();
    _state = new SurveyState();
    _students = new HashMap<Integer, Student>();
  }

  public void cancel() {
    // TODO: Intrepertation of States
  }

  public void open() {
    // TODO: Intrepertation of States
  }

  public void close() {
    // TODO: Intrepertation of States
  }

  public void finalize() {
    // TODO: Intrepertation of States
  }

  public boolean canBeRemoved() {
    // TODO: Intrepertation of States
    return false;
  }

  public void submitEntry() {
    // TODO: Intrepertation of States
  }

  public ArrayList<SurveyEntry> getResults() {
    return _entries;
  }
}
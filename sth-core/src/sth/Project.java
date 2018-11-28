package sth;

import java.io.Serializable;
import java.util.ArrayList;

import sth.exceptions.DuplicateSurveyProjectException;
import sth.exceptions.NoSurveyProjectException;
import sth.exceptions.OpeningSurveyProjectException;

public class Project implements Serializable {
  private static final long serialVersionUID = 201810051538L;

  private String _name;
  private String _description;
  private boolean _open;
  private ArrayList<Submission> _submissions;
  private Survey _survey;

  public Project(String name) {
    _name = name;
    _description = "";
    _open = true;
    _submissions = new ArrayList<Submission>();
    // ASk: Are we supposed to initialize survey? Or doesnt exist until explicit creation by representative
  }

  public String getName() {
    return _name;
  }

  public String getDescription() {
    return _description;
  }

  public boolean isOpen() {
    return _open;
  }

  public void addSubmission(Submission s) {
    if (_open)
      _submissions.add(s);
  }

  public void close() {
    // ASK: This exception will never be thrown
    // TODO: Solve problem from above
    _open = false;
    if (_survey != null) {
      try {
        _survey.open();
      } catch (OpeningSurveyProjectException e) {}
    }
  }

  public void createSurvey() throws DuplicateSurveyProjectException {
    if (_survey != null) {
      throw new DuplicateSurveyProjectException(_name);
    }
    
    _survey = new Survey(this);
  }

  public Survey getSurvey() throws NoSurveyProjectException {
    if (_survey == null) {
      throw new NoSurveyProjectException(_name);
    }
    return _survey;
  }

  public void removeSurvey() {
    _survey = null;
  }
}
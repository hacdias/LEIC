package sth;

import java.io.Serializable;
import java.util.TreeMap;

import sth.exceptions.DuplicateSurveyProjectException;
import sth.exceptions.NoSurveyProjectException;
import sth.exceptions.OpeningSurveyProjectException;
import sth.exceptions.NoSuchProjectOpenException;

import sth.exceptions.DuplicateSurveyProjectException;
import sth.exceptions.NoSurveyProjectException;
import sth.exceptions.OpeningSurveyProjectException;

public class Project implements Serializable {
  private static final long serialVersionUID = 201810051538L;

  private String _name;
  private String _description;
  private boolean _open;
  private TreeMap<Integer, Submission> _submissions;
  private Survey _survey;
  private Discipline _discipline;

  public Project(String name, Discipline d) {
    _name = name;
    _description = "";
    _open = true;
    _discipline = d;
    _submissions = new TreeMap<Integer, Submission>();
    // ASK: Are we supposed to initialize survey? Or doesnt exist until explicit
    // creation by representative
  }

  public String getName() {
    return _name;
  }

  public Discipline getDiscipline() {
    return _discipline;
  }

  public String getDescription() {
    return _description;
  }

  public boolean isOpen() {
    return _open;
  }

  public void addSubmission(Submission submission) throws NoSuchProjectOpenException {
    if (!_open) {
      throw new NoSuchProjectOpenException(getName());
    }
    
    int id = submission.getStudent().getId();
    _submissions.put(id, submission);
  }

  public boolean studentSubmited(Student s) {
    return _submissions.get(s.getId()) != null;
  }

  public void close() throws OpeningSurveyProjectException {
    _open = false;
    if (_survey != null) {
      _survey.open();
    }
  }

  public String getSubmissions() {
    String submissions = "";
    for (Submission sub : _submissions.values()) {
      submissions += sub.toString() + "\n";
    }
    return submissions;
  }

  public int getNumberSubmissions() {
    return _submissions.size();
  }

  public Survey createSurvey() throws DuplicateSurveyProjectException {
    if (_survey != null) {
      throw new DuplicateSurveyProjectException(_name);
    }

    _survey = new Survey(this);
    return _survey;
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
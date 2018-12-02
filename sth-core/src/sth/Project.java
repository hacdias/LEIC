package sth;

import java.io.Serializable;
import java.util.TreeSet;

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
  private TreeSet<Submission> _submissions;
  private Survey _survey;
  private Discipline _discipline;

  public Project(String name, Discipline d) {
    _name = name;
    _description = "";
    _open = true;
    _discipline = d;
    _submissions = new TreeSet<Submission>();
    // ASk: Are we supposed to initialize survey? Or doesnt exist until explicit creation by representative
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
    // TODO: Test this out
    if (_open) {
      if (_submissions.contains(submission))
        _submissions.remove(submission);

      _submissions.add(submission);
    } else {
      throw new NoSuchProjectOpenException(getName());
    }
  }

  public boolean studentSubmited(Student s) {
    // TODO: Dont know if it is a good way to check if submited
    for (Submission sub : _submissions) {
      if (s.compareTo(sub.getStudent()) == 0)
        return true;
    }

    return false;
  }

  public void close() {
    // ASK: This exception will never be thrown
    // TODO: Solve problem from above BLANCK CATCH IMPORTANT
    _open = false;
    if (_survey != null) {
      try {
        _survey.open();
      } catch (OpeningSurveyProjectException e) {}
    }
  }

  public String getSubmissions() {
    String submissions = "";
    for (Submission sub : _submissions) {
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
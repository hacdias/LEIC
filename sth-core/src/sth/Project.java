package sth;

import java.io.Serializable;
import java.util.ArrayList;

public class Project implements Serializable {
  private static final long serialVersionUID = 201810051538L;

  private String _name;
  /* TODO: get description
  private String _description; */
  private boolean _open;
  private ArrayList<Submission> _submissions;
  private Survey _survey;

  public Project(String name) {
    _name = name;
    _open = true;
    _submissions = new ArrayList<Submission>();
    _survey = new Survey();
  }

  public String getName() {
    return _name;
  }

  public void addSubmission(Submission s) {
    if (_open)
      _submissions.add(s);
  }

  public void close() {
    _open = false;
  }
}
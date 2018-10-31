package sth;

import java.io.Serializable;
import java.util.ArrayList;

public class Project implements Serializable {
  private static final long serialVersionUID = 201810051538L;

  private String _name;
  private String _description;
  private boolean _open;
  private ArrayList<Submission> _submissions;
  private Survey _survey;

  public Project(String name, String description) {
    _name = name;
    _description = description;
    _open = true;
    _submissions = new ArrayList<Submission>();
    _survey = new Survey();
  }

  public boolean addSubmission(Submission s) {
    _submissions.add(s);
    return true;
  }

  public void close() {
    _open = false;
  }
}
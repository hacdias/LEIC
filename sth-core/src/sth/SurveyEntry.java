package sth;

import java.io.Serializable;

public class SurveyEntry implements Serializable {
  private static final long serialVersionUID = 201810051538L;
  
  private double _spentHours;
  private String _comment;

  SurveyEntry(double spentHours, String comment) {
    _spentHours = spentHours;
    _comment = comment;
  }

  /**
   * @return the spent hours
   */
  public double getSpentHours() {
    return _spentHours;
  }

  /**
   * @return the comment
   */
  public String getComment() {
    return _comment;
  }
}
package sth;

public class SurveyStats {
  private double _avgTime;
  private double _minTime;
  private double _maxTime;

  SurveyStats (double avg, double min, double max) {
    _avgTime = avg;
    _maxTime = max;
    _minTime = min;
  }

  public double getAvg () {
    return _avgTime;
  }

  public double getMin () {
    return _minTime;
  }

  public double getMax () {
    return _maxTime;
  }
}

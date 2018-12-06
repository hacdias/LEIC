package sth;

public class SurveyStats {
  private int _avgTime;
  private int _minTime;
  private int _maxTime;

  SurveyStats (int avg, int min, int max) {
    _avgTime = avg;
    _maxTime = max;
    _minTime = min;
  }

  public int getAvg () {
    return _avgTime;
  }

  public int getMin () {
    return _minTime;
  }

  public int getMax () {
    return _maxTime;
  }
}

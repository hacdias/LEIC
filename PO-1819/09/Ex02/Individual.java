public class Individual extends Taxpayer {
  private int _tax;

  Individual (int x) {
    _tax = x;
  }

  public int pay () {
    return _tax;
  }
}
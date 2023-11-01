package tables;

public class EqualTo implements SelectionPredicate {
  private int _equalTo;

  EqualTo (int n) {
    _equalTo = n;
  }

  @Override
  public boolean ok (int n) {
    return n == _equalTo;
  }
}

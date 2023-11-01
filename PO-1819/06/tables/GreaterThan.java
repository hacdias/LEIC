public class GreaterThan implements SelectionPredicate {
  private int _greatThan;

  GreaterThan (int n) {
    _greatThan = n;
  }

  @Override
  public boolean ok (int n) {
    return n > _greatThan;
  }
}

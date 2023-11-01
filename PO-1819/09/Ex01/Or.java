public class Or extends BinaryOperator {
  public Or (Validator left, Validator right) {
    super(left, right);
  }

  @Override
  public boolean ok (String str) {
    return _left.ok(str) || _right.ok(str);
  }
}

public class Not extends UnaryOperator {
  public Not (Validator v) {
    super(v);
  }

  @Override
  public boolean ok (String str) {
    return !_validator.ok(str);
  }
}
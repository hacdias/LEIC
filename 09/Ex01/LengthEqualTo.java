public class LengthEqualTo extends IntegerValidator {
  LengthEqualTo (int ref) {
    super(ref);
  }

  public boolean ok (String str) {
    return str.length() == _ref;
  }
}
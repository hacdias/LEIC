public class LengthLesserThan extends IntegerValidator {
  LengthLesserThan (int ref) {
    super(ref);
  }

  public boolean ok (String str) {
    return str.length() < _ref;
  }
}
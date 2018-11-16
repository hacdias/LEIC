public class LengthGreaterThan extends IntegerValidator {
  LengthGreaterThan (int ref) {
    super(ref);
  }

  public boolean ok (String str) {
    return str.length() > _ref;
  }
}
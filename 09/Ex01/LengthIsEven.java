public class LengthIsEven extends Validator {
  public boolean ok (String str) {
    return str.length() % 2 == 0;
  }
}
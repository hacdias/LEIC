public class NonZeroLength extends Validator {
  public boolean ok (String str) {
    return str.length() != 0;
  }
}
public abstract class UnaryOperator extends Validator {
  protected Validator _validator;

  UnaryOperator (Validator v) {
    _validator = v;
  }
}

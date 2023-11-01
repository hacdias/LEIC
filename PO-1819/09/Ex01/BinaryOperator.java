public abstract class BinaryOperator extends Validator {
  protected Validator _left, _right;

  BinaryOperator (Validator left, Validator right) {
    _left = left;
    _right = right;
  }
}

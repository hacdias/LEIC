public class AndGate2 {
  private boolean _a = false;
  private boolean _b = false;

  AndGate2() {}

  AndGate2(boolean v) {
    _a = _b = v;
  }

  AndGate2(boolean a, boolean b) {
    _a = a;
    _b = b;
  }

  public boolean getA() {
    return _a;
  }

  public void setA(boolean _a) {
    this._a = _a;
  }
  
  public boolean getB() {
    return _b;
  }

  public void setB(boolean _b) {
    this._b = _b;
  }

  public void setBoth (boolean v) {
    _a = _b = v;
  }

  public boolean getOutput() {
    return _a && _b;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AndGate2) {
      AndGate2 g = (AndGate2)obj;
      return _a == g.getA() && _b == g.getB();
    }

    return false;
  }

  @Override
  public String toString() {
    return "A:" + _a + " B:" + _b;
  }
}
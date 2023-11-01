public class AndGate3i extends AndGate2 {
  private boolean _c = false;

  AndGate3i() {}

  AndGate3i (boolean v) {
    super(v);
    _c = v;
  }

  AndGate3i(boolean a, boolean b, boolean c) {
    super(a, b);
    _c = c;
  }

  public boolean getC() {
    return _c;
  }

  public void setC(boolean c) {
    _c = c;
  }

  @Override
  public boolean getOutput() {
    return super.getOutput() && _c;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AndGate3i) {
      AndGate3i g = (AndGate3i)obj;
      return super.equals(obj) && _c == g.getC();
    }

    return false;
  }

  @Override
  public String toString() {
    return super.toString() + " C:" + _c;
  }
}
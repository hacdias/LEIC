public class AndGate3c {
  private AndGate2 _gate1;
  private AndGate2 _gate2;

  AndGate3c () {
    _gate1 = new AndGate2();
    _gate2 = new AndGate2(_gate1.getOutput(), false);
  }

  AndGate3c (boolean v) {
    _gate1 = new AndGate2(v);
    _gate2 = new AndGate2(_gate1.getOutput(), v);
  }

  AndGate3c (boolean a, boolean b, boolean c) {
    _gate1 = new AndGate2(a, b);
    _gate2 = new AndGate2(_gate1.getOutput(), c);
  }

  public boolean getA() {
    return _gate1.getA();
  }

  public void setA(boolean a) {
    _gate1.setA(a);
    _gate2.setA(_gate1.getOutput());
  }

  public boolean getB() {
    return _gate1.getB();
  }

  public void setB(boolean b) {
    _gate1.setB(b);
    _gate2.setA(_gate1.getOutput());
  }

  public boolean getC() {
    return _gate2.getB();
  }

  public void setC(boolean c) {
    _gate2.setB(c);
  }

  public boolean getOutput() {
    return _gate2.getOutput();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof AndGate3c) {
      AndGate3c gt = (AndGate3c)o;

      return getA() == gt.getA() && getB() == gt.getB() && getC() == gt.getC();
    }
    
    return false;
  }

  @Override
  public String toString() {
    return "A:" + getA() + " B:" + getB() + " C:" + getC();
  }

}
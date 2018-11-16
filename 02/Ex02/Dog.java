public class Dog {
  private static final int BASE_ENERGY = 1000;
  private int _energy = BASE_ENERGY;
  private String _name;

  Dog (String n) {
    _name = n;
  }

  public String getName() {
    return _name;
  }

  public void setName(String _name) {
    this._name = _name;
  }

  public int getEnergy() {
    return _energy;
  }

  public boolean run () {
    if (_energy < 50) {
      return false;
    }

    _energy -= 50;
    return true;
  }

  public void sleep () {
    _energy = BASE_ENERGY;
  }

  public boolean caughtRat (Rat r) {
    run();
    r.run();

    if ((int) 25 * Math.random() == 0) {
      return true;
    }

    r.escaped();
    return false;
  }

  public void eatRat (Rat r) {
    if (caughtRat(r)) {
      _energy += r.drain();
    }
  }

  public void attackCat (Cat c) {
    _energy -= 100;
    c.attacked(25);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Dog) {
      Dog d = (Dog)obj;
      return _energy == d.getEnergy() &&
        _name.equals(d.getName());
    }

    return false;
  }

  @Override
  public String toString() {
    return "Dog " + _name + " with " + _energy + " energy.";
  }
}
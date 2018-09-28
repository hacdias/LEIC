public class Cat {
  private static final int BASE_ENERGY = 500;
  private int _energy = BASE_ENERGY;
  private String _name;

  Cat (String n) {
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
    if (_energy < 25) {
      return false;
    }

    _energy -= 25;
    return true;
  }

  public void sleep () {
    _energy = BASE_ENERGY;
  }

  public boolean caughtRat (Rat r) {
    run();
    r.run();

    if ((int) 5 * Math.random() == 0) {
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

  public boolean caughtBird (Bird b) {
    run();
    b.run();

    if ((int) 10 * Math.random() == 0) {
      return true;
    }

    b.escaped();
    return false;
  }

  public void eatBird (Bird b) {
    if (caughtBird(b)) {
      _energy += b.drain();
    }
  }

  public void attacked (int a) {
    _energy -= a;
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
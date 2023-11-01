public class Bird {
  private static final int BASE_ENERGY = 20;
  private int _energy = BASE_ENERGY;

  public int getEnergy() {
    return _energy;
  }

  public boolean run () {
    if (_energy < 5) {
      return false;
    }

    _energy -= 5;
    return true;
  }

  public boolean fly () {
    if (_energy < 2) {
      return false;
    }

    _energy -= 2;
    return true;
  }

  public void escaped () {
    _energy += 5;
  }

  public int drain () {
    int energy = _energy;
    _energy = 0;
    return energy;
  }

  public void sleep () {
    _energy = BASE_ENERGY;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Bird) {
      Bird b = (Bird)obj;
      return _energy == b.getEnergy();
    }

    return false;
  }

  @Override
  public String toString() {
    return "Bird with " + _energy + " energy.";
  }
}
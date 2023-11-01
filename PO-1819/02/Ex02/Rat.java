public class Rat {
  private static final int BASE_ENERGY = 50;
  private int _energy = BASE_ENERGY;

  public int getEnergy() {
    return _energy;
  }

  public boolean run () {
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
    if (obj instanceof Rat) {
      Rat r = (Rat)obj;
      return _energy == r.getEnergy();
    }

    return false;
  }

  @Override
  public String toString() {
    return "Rat with " + _energy + " energy.";
  }
}
public class Rat extends Animal {
  Rat () {
    super(50, 2);
  }

  public void escaped () {
    increaseEnergy(5);
  }

  public int drain () {
    int energy = getEnergy();
    setEnergy(0);
    return energy;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Rat) {
      return super.equals(obj);
    }

    return false;
  }

  @Override
  public String toString() {
    return "Rat with " + getEnergy() + " energy.";
  }
}
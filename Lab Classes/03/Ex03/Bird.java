public class Bird extends Animal {
  Bird () {
    super(20, 5);
  }

  public boolean fly () {
    if (getEnergy() < 2) {
      return false;
    }

    increaseEnergy(-2);
    return true;
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
    if (obj instanceof Bird) {
      return super.equals(obj);
    }

    return false;
  }

  @Override
  public String toString() {
    return "Bird with " + getEnergy() + " energy.";
  }
}
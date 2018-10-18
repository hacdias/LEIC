package energy;

public class Bird extends AnimalPrey {
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
package energy;

public class Rat extends AnimalPrey {
  Rat () {
    super(50, 2);
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
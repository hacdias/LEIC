package energy;

public abstract class AnimalPrey extends Animal implements Prey {
  AnimalPrey (int baseEnergy, int runEnergy) {
    super(baseEnergy, baseEnergy);
  }

  @Override
  public void escaped () {
    increaseEnergy(5);
  }

  @Override
  public int drain () {
    int energy = getEnergy();
    setEnergy(0);
    return energy;
  }
}
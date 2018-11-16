package energy;

public abstract class Animal {
  private final int _baseEnergy;
  private final int _runEnergy;
  private int _energy;

  Animal (int baseEnergy, int energy, int runEnergy) {
    _baseEnergy = baseEnergy;
    _energy = Math.min(baseEnergy, energy);
    _runEnergy = Math.min(baseEnergy, runEnergy);
  }
  
  Animal (int baseEnergy, int runEnergy) {
    this(baseEnergy, baseEnergy, runEnergy);
  }

  public int getBaseEnergy () {
    return _baseEnergy;
  }

  public int getRunEnergy () {
    return _runEnergy;
  }

  public int getEnergy () {
    return _energy;
  }

  public void setEnergy (int e) {
    _energy = e;
  }

  public void increaseEnergy (int e) {
    _energy += e;
  }

  public boolean run () {
    if (_energy < _runEnergy) {
      return false;
    }

    _energy -= _runEnergy;
    return true;
  }

  public void sleep () {
    _energy = _baseEnergy;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Animal) {
      Animal a = (Animal)obj;
      return getBaseEnergy() == a.getBaseEnergy() &&
        getRunEnergy() == a.getRunEnergy() &&
        getEnergy() == a.getEnergy();
    }

    return false;
  }

  @Override
  public String toString() {
    return "base energy:" + getBaseEnergy() + "; run energy:" + getRunEnergy() + "; energy:" + getEnergy();
  }
}
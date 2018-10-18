package energy;

public abstract class NamedAnimal extends Animal implements Predator {
  private String _name;
  private int _catchRate;
  
  NamedAnimal(int baseEnergy, int energy, int runEnergy, int catchRate, String name) {
    super(baseEnergy, energy, runEnergy);
    _catchRate = catchRate;
    _name = name;
  }

  NamedAnimal(int baseEnergy, int runEnergy, int catchRate, String name) {
    super(baseEnergy, runEnergy);
    _catchRate = catchRate;
    _name = name;
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
  }

  @Override
  public boolean caught (Prey p) {
    run();
    p.run();

    if ((int) _catchRate * Math.random() == 0) {
      return true;
    }

    p.escaped();
    return false;
  }

  @Override
  public void eat (Prey p) {
    if (caught(p)) {
      increaseEnergy(p.drain());
    }
  }

  @Override
  public boolean equals(Object obj) {
      if (obj instanceof Animal) {
        NamedAnimal a = (NamedAnimal)obj;
        return super.equals(a) && _name == a.getName();
      }

      return false;
  }

  @Override
	public String toString() {
		return _name + ", " + super.toString();
	}
}
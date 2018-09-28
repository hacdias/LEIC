public class NamedAnimal extends Animal {
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

  public boolean caughtRat (Rat r) {
    run();
    r.run();

    if ((int) _catchRate * Math.random() == 0) {
      return true;
    }

    r.escaped();
    return false;
  }

  public void eatRat (Rat r) {
    if (caughtRat(r)) {
      increaseEnergy(r.drain());
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
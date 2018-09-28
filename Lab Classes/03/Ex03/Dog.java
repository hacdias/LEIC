public class Dog extends NamedAnimal {
  Dog (String name) {
    super(1000, 50, 25, name);
  }

  public void attackCat (Cat c) {
    increaseEnergy(-100);
    c.attacked(25);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Dog) {
      return super.equals(obj);
    }

    return false;
  }
}
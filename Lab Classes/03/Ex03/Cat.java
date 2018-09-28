public class Cat extends NamedAnimal {
  Cat (String name) {
    super(500, 25, 5, name);
  }

  public boolean caughtBird (Bird b) {
    run();
    b.run();

    if ((int) 10 * Math.random() == 0) {
      return true;
    }

    b.escaped();
    return false;
  }

  public void eatBird (Bird b) {
    if (caughtBird(b)) {
      increaseEnergy(b.drain());
    }
  }

  public void attacked (int a) {
    increaseEnergy(-a);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Cat) {
      return super.equals(obj);
    }

    return false;
  }
}
public class Cat {
  private int _age = 0;
  private float _weight = 0;
  private String _name = "";

  public Cat(int age, float weight, String name) {
    _age = age;
    _weight = weight;
    _name = name;
  }

  public int getAge() {
    return _age;
  }

  public void setAge(int age) {
    _age = age;
  }

  public float getWeight() {
    return _weight;
  }

  public void setWeight(float weight) {
    _weight = weight;
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
  }

  public String toString() {
    return "Cat [_age=" + _age + ", _weight=" + _weight + ", _name=" + _name + "]";
  }
}

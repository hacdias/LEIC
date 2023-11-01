import java.io.Serializable;

public class Cat implements Serializable {
  private String name;
  private int age;
  private int weight;

  public Cat(String name, int age, int weight) {
    this.name = name;
    this.age = age;
    this.weight = weight;
  }

  /**
   * @return the age
   */
  public int getAge() {
    return age;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the weight
   */
  public int getWeight() {
    return weight;
  }

  /**
   * @param age the age to set
   */
  public void setAge(int age) {
    this.age = age;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @param weight the weight to set
   */
  public void setWeight(int weight) {
    this.weight = weight;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Cat) {
      Cat c = (Cat) o;
      return this.name == c.name && this.age == c.age && this.weight == c.weight;
    }

    return false;
  }

  @Override
  public String toString() {
    return "CAT " + this.name + " is " + this.age + " years old and weights " + this.weight + " kgs";
  }
}

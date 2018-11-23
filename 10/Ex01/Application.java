import java.util.*;

class Animal implements Comparable<Animal> {
  private Double _weight;
  public Animal(double weight) { _weight = weight; }
  public Double getWeight()         { return _weight; }
  public int    compareTo(Animal a) { return _weight.compareTo(a.getWeight()); }
  public String toString()          { return "Peso " + _weight; }
}

class Cat extends Animal {
  private Integer _age;
  public Cat(int age, double weight) { super(weight); _age = age; }
  public Integer getAge()   { return _age; }
  public String  toString() { return super.toString() + " e idade " + _age; }
}

class AgeComparator implements Comparator<Cat> {
  @Override
  public int compare(Cat c1, Cat c2) {
    return c1.getAge().compareTo(c2.getAge());
  }
}

public class Application {
  public static void main(String args[]) {
    List<Cat> cats = new ArrayList<Cat>();
    cats.add(new Cat(1, 8));
    cats.add(new Cat(2, 7));
    cats.add(new Cat(3, 6));

    Collections.sort(cats);       // ordenação por peso (_weight)
    for (Cat cat: cats) System.out.println(cat);

    Collections.sort(cats, new AgeComparator());
    for (Cat cat: cats) System.out.println(cat);
  }
}
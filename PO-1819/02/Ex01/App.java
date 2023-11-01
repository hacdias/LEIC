public class App {
  public static void main(String[] args) {
    Cat c1 = new Cat("Henrique", 18, 64);
    Cat c2 = new Cat("Matilde", 19, 65);
    
    System.out.println(c1.toString());
    System.out.println(c2.toString());

    System.out.println("Are the cats equal?");
    System.out.println(c1.equals(c2));

    c2.setName("Henrique");
    c2.setAge(18);
    c2.setWeight(64);

    System.out.println("Are they equal now?");
    System.out.println(c1.equals(c2));
  }
}
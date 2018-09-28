public class App {
  public static void main(String[] args) {
    Dog d1 = new Dog("Piloto");
    Dog d2 = new Dog("Átila");

    Cat c1 = new Cat("Tareco");
		Cat c2 = new Cat("Pantufa");
    Cat c3 = new Cat("Kitty");
    
    Bird[] birds = new Bird[20];
    for (int i = 0; i < birds.length; i++)
      birds[i] = new Bird();

    Rat[] rats = new Rat[20];
    for (int i = 0; i < rats.length; i++)
      rats[i] = new Rat();

    System.out.println("BEFORE");
    System.out.println(d1);
    System.out.println(d2);
    System.out.println(c1);
    System.out.println(c2);
    System.out.println(c3);
  
    for (int i = 0; i < birds.length; i++)
      System.out.println(birds[i]);
  
    for (int i = 0; i < rats.length; i++)
      System.out.println(rats[i]);

    for (int ix = 0; ix < birds.length; ix++)
			birds[ix].fly();
 
		d1.run();
		d2.attackCat(c1);
		c2.eatBird(birds[2]);
		c3.eatBird(birds[9]);
		c3.eatRat(rats[7]);
		d2.eatRat(rats[12]);
    rats[3].run();
    
    System.out.println("AFTER");
    System.out.println(d1);
    System.out.println(d2);
    System.out.println(c1);
    System.out.println(c2);
    System.out.println(c3);
  
    for (int i = 0; i < birds.length; i++)
      System.out.println(birds[i]);
  
    for (int i = 0; i < rats.length; i++)
      System.out.println(rats[i]);
  }
}

package energy;

public interface Predator {
  boolean run();
  boolean caught(Prey prey);
  void eat(Prey prey);
}
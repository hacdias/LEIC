package sth;

public interface Observable {
  public void attach(Observer p);

  public void detach(Observer p);

  public void notify(String s);
}

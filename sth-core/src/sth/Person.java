package sth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.lang.Comparable;

public abstract class Person implements Serializable, Comparable<Person> {
  private static final long serialVersionUID = 201810051538L;
  
  final private String _name;
  final private int _id;
  private ArrayList<String> _notifications;
  private String _phoneNumber;

  public static final Comparator<Person> NAME_COMPARATOR = new NameComparator();

  Person(int id,  String phoneNumber, String name) {
    _name = name;
    _phoneNumber = phoneNumber;
    _id = id;
    _notifications = new ArrayList<String>();
  }

  public String getName() {
    return _name;
  }

  public String getPhoneNumber() {
    return _phoneNumber;
  }
  
  public void setPhoneNumber(String phoneNumber) {
    _phoneNumber = phoneNumber;
  }

  public int getId() {
    return _id;
  }

  public void clearNotifications() {
    _notifications.clear();
  }

  public void notify(String n) {
    _notifications.add(n);
  }

  @Override
  public String toString() {
    return _id + "|" + _phoneNumber + "|" + _name;
  }

  @Override
  public int compareTo(Person p) {
    return getId() - p.getId();
  }

  private static class NameComparator implements Comparator<Person>, Serializable {
    private static final long serialVersionUID = 201810051538L;
    
    @Override
    public int compare(Person p1, Person p2) {
      return p1.getName().compareTo(p2.getName());
    }
  }
}

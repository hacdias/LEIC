package sth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.lang.Comparable;

public abstract class Person implements Comparable<Person>, Serializable {
  private ArrayList<Notification> _notifications;
  private String _name;
  private String _phoneNumber;
  private int _id;

  public static final Comparator<Person> NAME_COMPARATOR = new NameComparator();

  Person(String name, String phoneNumber, int id) {
    _name = name;
    _phoneNumber = phoneNumber;
    _id = id;
    _notifications = new ArrayList<Notification>();
  }

  public String getName() {
    return _name;
  }

  public String getPhoneNumber() {
    return _phoneNumber;
  }

  public int getId() {
    return _id;
  }

  public void clearNotifications() {
    _notifications.clear();
  }

  public void notify(Notification n) {
    _notifications.add(n);
  }

  public void setPhoneNumber(String phoneNumber) {
    _phoneNumber = phoneNumber;
  }

  @Override
  public int compareTo(Person p) {
    return getId() - p.getId();
  }

  private static class NameComparator implements Comparator<Person> {
    @Override
    public int compare(Person p1, Person p2) {
      return p1.getName().compareTo(p2.getName());
    }
  }
}

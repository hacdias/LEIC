package sth;

import java.util.ArrayList;

public abstract class Person {
  private ArrayList<Notification> _notifications;
  private String _name;
  private String _phoneNumber;
  private int _id;

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
}

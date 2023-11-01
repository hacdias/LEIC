package sth;

import java.io.Serializable;

public class Administrative extends Person {
  private static final long serialVersionUID = 201810051538L;

  Administrative(int id, String phoneNumber, String name) {
    super(id, phoneNumber, name);
  }

  public String accept(UserDescription u) {
    return u.descAdministrative(this);
  }
}

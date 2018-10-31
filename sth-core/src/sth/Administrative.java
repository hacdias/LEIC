package sth;

import java.io.Serializable;

public class Administrative extends Person implements Serializable {
  private static final long serialVersionUID = 201810051538L;
  
  Administrative(int id,  String phoneNumber, String name) {
    super(id, phoneNumber, name);
  }

  @Override
  public String toString() {
    return "FUNCIONÁRIO|" + super.toString();
  }
}

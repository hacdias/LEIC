package sth;

import java.io.Serializable;

public class Administrative extends Person implements Serializable {
  private static final long serialVersionUID = 201810051538L;
  
  Administrative(String name, String phoneNumber, int id) {
    super(name, phoneNumber, id);
  }
}

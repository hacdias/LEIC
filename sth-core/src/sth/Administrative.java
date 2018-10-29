package sth;

import java.io.Serializable;

public class Administrative extends Person implements Serializable {
  Administrative(String name, String phoneNumber, int id) {
    super(name, phoneNumber, id);
  }
}

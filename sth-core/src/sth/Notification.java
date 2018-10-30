package sth;

import java.io.Serializable;

public class Notification implements Serializable {
  private static final long serialVersionUID = 201810051538L;
  
  private Person _sender;
  private String _message;

  Notification(Person sender, String message) {
    _sender = sender;
    _message = message;
  }

  /**
   * @return the sender
   */
  public Person getSender() {
    return _sender;
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return _message;
  }
}

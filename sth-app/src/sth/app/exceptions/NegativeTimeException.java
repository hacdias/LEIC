package sth.app.exceptions;

import pt.tecnico.po.ui.DialogException;

public class NegativeTimeException extends DialogException {
  private static final long serialVersionUID = 201810051538L;
  private double _time;
  
  public NegativeTimeException(double time) {
    _time = time;
  }

  /** @see pt.tecnico.po.ui.DialogException#getMessage() */
  @Override
  public String getMessage() {
    return "Tempo inválido (tem que ser maior ou igual a 0): " + _time;
  }
}

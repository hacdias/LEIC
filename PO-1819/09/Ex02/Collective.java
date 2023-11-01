import java.util.ArrayList;

public abstract class Collective extends Taxpayer {
  protected ArrayList<Taxpayer> _payers;

  public int pay () {
    int tax = 0;

    for (Taxpayer p : _payers) {
      tax += p.pay();
    }

    return tax;
  }
}

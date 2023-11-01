package tables;

public class Table {
  private int _vector[];

  Table (int c) {
    _vector = new int[c];
  }

  public void insert (int pos, int value) {
    _vector[pos] = value;
  }

  public boolean contains (SelectionPredicate p) {
    for (int x : _vector) {
      if (p.ok(x)) {
        return true;
      }
    }

    return false;
  }
}
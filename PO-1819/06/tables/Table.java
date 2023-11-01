import java.util.Comparator;

public class Table implements Comparable<Table>, Iterable {
  private int _vector[];

  public static final Comparator<Table> MAX_COMPARATOR = new MaxComparator();
  public static final Comparator<Table> LENGTH_COMPARATOR = new LengthComparator();

  private static class MaxComparator implements Comparator<Table> {
    @Override
    public int compare(Table t1, Table t2) {
      return t1.getMax() - t2.getMax();
    }
  }

  private static class LengthComparator implements Comparator<Table> {
    @Override
    public int compare(Table t1, Table t2) {
      return t1.getLength() - t2.getLength();
    }
  }

  private class It implements Iterator {
    private int _curr = 0;

    public boolean hasNext () {
      return _curr < getLength();
    }

    public int next () {
      _curr++;
      return getPos(_curr);
    }
  }

  public Iterator getIterator () {
    return new It();
  }

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

	public int getMax() {
		int max = _vector[0];
		for (int i : _vector)
			if (i > max)
				max = i;
		return max;
  }
  
  public int getPos (int pos) {
    return _vector[pos];
  }
 
	public int getLength() {
		return _vector.length;
	}

  public int getSum () {
    int sum = 0;
    for (int i : _vector) sum += i;
    return sum;
  }

  @Override
  public int compareTo(Table o) {
    return getSum() - o.getSum();
  }
}
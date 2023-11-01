import java.util.TreeMap;

class Album {
  private TreeMap<Integer, Card> _cards;

  Album () {
    _cards = new TreeMap<Integer, Card>();
  }

  public void add (Card c) {
    _cards.put(c.getNum(), c);
  }

  public void remove (int n) {
    _cards.remove(n);
  }

  public TreeMap<Integer, Card> getAll() {
    return _cards;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Album) {
      Album a = (Album)obj;
      return _cards.size() == obj.getAll().size();
    }

    return false;
  }
}
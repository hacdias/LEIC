import java.lang.Comparable;

class Card implements Comparable<Card> {
  private Image _img;
  private int _num;

  Card (Image img, int n) {
    _img = img;
    _num = n;
  }

  /**
   * @return the _img
   */
  public Image getImage() {
    return _img;
  }

  /**
   * @return the _num
   */
  public int getNum() {
    return _num;
  }

  @Override
  public int compareTo(Card o) {
    return _num == o.getNum();
  }
}

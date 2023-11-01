public class TextSpan implements TextItem {
  private String _text;

  public TextSpan(String text) {
      _text = text;
  }

  @Override
  public String render() {
      return "<span>" + _text + "</span>";
  }
}

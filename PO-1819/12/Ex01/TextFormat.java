public abstract class TextFormat implements TextItem {
  private TextItem _textItem;

  public TextFormat(TextItem textItem) {
      _textItem = textItem;
  }

  public TextItem getTextItem() {
      return _textItem;
  }

  public String render() {
      return _textItem.render();
  }
}

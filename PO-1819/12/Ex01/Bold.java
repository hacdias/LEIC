public class Bold extends TextFormat {

  public Bold(TextItem textItem) {
      super(textItem);
  }

  @Override
  public String render() {
      return "<b>" + super.render() + "</b>";
  }
}

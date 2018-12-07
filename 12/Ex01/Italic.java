public class Italic extends TextFormat {

  public Italic(TextItem textItem) {
      super(textItem);
  }

  @Override
  public String render() {
      return "<i>" + super.render() + "</i>";
  }
}

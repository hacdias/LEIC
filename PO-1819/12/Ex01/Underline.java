public class Underline extends TextFormat {

  public Underline(TextItem textItem) {
      super(textItem);
  }

  @Override
  public String render() {
      return "<u>" + super.render() + "</u>";
  }
}

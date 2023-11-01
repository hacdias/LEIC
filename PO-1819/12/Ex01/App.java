public class App {
  public static void main(String[] args) {
      TextItem span1 = new TextSpan("BATATA");
      TextItem text1 = new Bold(new Italic(span1));
      System.out.println(text1.render());
      
      TextItem span2 = new TextSpan("CEBOLA");
      TextItem text2 = new Underline(new Bold(new Italic(span2)));
      System.out.println(text2.render());
  }
}
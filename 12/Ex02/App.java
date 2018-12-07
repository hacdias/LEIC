import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.File;

public class App {
  public static void main(String[] args) {
    Cat c = new Cat(10, 15, "Tareco");

    try {
      CatOutputChannel out = new CatOutputChannel(new DataOutputStream(new FileOutputStream(new File("output.dat"))));
      out.put(c);
    } catch (FileNotFoundException e) {

    } catch (IOException e) {

    }
  }
}
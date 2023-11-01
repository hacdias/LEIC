import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

public class App {
  public static void main(String[] args) {
    Cat c = new Cat(10, 15, "Tareco");

    try {
      CatOutputChannel out = new CatOutputChannel(new DataOutputStream(new FileOutputStream(new File("output.dat"))));
      out.put(c);
      out.close();
      CatInputChannel in = new CatInputChannel(new DataInputStream(new FileInputStream(new File("output.dat"))));
      in.get();
      in.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
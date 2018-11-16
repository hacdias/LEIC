import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadFile {
  public static void main(String[] args) {
    try {
      BufferedReader bf = new BufferedReader(new FileReader(args[0]));
      String line;
      String maxLine = "";

      while ((line = bf.readLine()) != null) {
        if (line.length() > maxLine.length()) {
          maxLine = line;
        }
      }

      System.out.println(maxLine);
  
      bf.close();
    } catch (IOException e) {
      System.out.println(e);
    }

  }
}
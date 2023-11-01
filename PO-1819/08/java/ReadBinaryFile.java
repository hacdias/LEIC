import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.UTFDataFormatException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class ReadBinaryFile {
  public static void main(String[] args) {
    Cat g = new Cat("OLA", 78, 98);
    
    try {
      DataOutputStream s = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("gato.dat")));

      s.writeInt(g.getAge());
      s.writeInt(g.getWeight());
      s.writeUTF(g.getName());

      s.close();

      DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream("gato.dat")));

      int age = in.readInt();
      int weight = in.readInt();
      String name = in.readUTF();

      System.out.println((new Cat(name, age, weight)).toString());


      in.close();
    } catch (EOFException|UTFDataFormatException e) {
      System.out.println(e);
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}
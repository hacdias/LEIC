import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class Seria {
  public static void main(String[] args) {
    Cat c1 = new Cat("Tareco", 58, 100);
    System.out.println(c1.toString());

    try {
      ObjectOutputStream out = new ObjectOutputStream(
        new BufferedOutputStream(
          new FileOutputStream("file.dat")
        )
      );

      out.writeObject(c1);
      out.close();
    } catch (InvalidClassException|NotSerializableException e) {
      e.printStackTrace();
    } catch(IOException e) {
      e.printStackTrace();
    }

    try {
      ObjectInputStream in = new ObjectInputStream(
        new BufferedInputStream(
          new FileInputStream("file.dat")
        )
      );

      Cat c2 = (Cat)in.readObject();
      System.out.println(c2.toString());
      in.close();
    } catch (ClassNotFoundException|NotSerializableException e) {
      e.printStackTrace();
    } catch(IOException e) {
      e.printStackTrace();
    }

  }
}

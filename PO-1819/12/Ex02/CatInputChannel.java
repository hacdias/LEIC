import java.io.DataInputStream;
import java.io.IOException;

public class CatInputChannel {
  private DataInputStream _input;
  private boolean _closed = false;

  CatInputChannel (DataInputStream o) {
    _input = o;
  }

  public Cat get () throws IOException {
    if (_closed) {
      throw new IOException("channel closed");
    }

    int age = _input.readInt();
    float weight = _input.readFloat();
    String name = _input.readUTF();
    return new Cat(age, weight, name);
  }

  public void close () throws IOException {
    _input.close();
    _closed = true;
  }
}
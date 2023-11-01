import java.io.DataOutputStream;
import java.io.IOException;

public class CatOutputChannel {
  private DataOutputStream _output;
  private boolean _closed = false;

  CatOutputChannel (DataOutputStream o) {
    _output = o;
  }

  public void put (Cat c) throws IOException {
    if (_closed) {
      throw new IOException("channel closed");
    }

    _output.writeInt(c.getAge());
    _output.writeFloat(c.getWeight());
    _output.writeUTF(c.getName());
  }

  public void close () throws IOException {
    _output.close();
    _closed = true;
  }
}
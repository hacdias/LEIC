package sth;

import java.io.Serializable;
import java.util.Comparator;
import java.text.Collator;
import java.util.Locale;

public class StringComparator implements Comparator<String>, Serializable {
  private static final long serialVersionUID = 201810051538L;

  @Override
  public int compare(String a, String b) {
    Collator comp = Collator.getInstance(Locale.getDefault());
    return comp.compare(a, b);
  }
}

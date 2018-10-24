#include <string>
#include <vector>
#include <iostream>

class Printer {
public:
  virtual std::string format(int n) = 0;
  virtual ~Printer() {};
};

class DuplicatePrinter: public Printer {
public:
  std::string format (int n) override {
    return std::to_string(n) + " " + std::to_string(n);
  }
};

class BracketsPrinter: public Printer {
public:
  std::string format (int n) override {
    return "[" + std::to_string(n) + "]";
  }
};

class Table {
  int _size;
  std::vector<int> _arr;

public:
  Table (int size): _size(size) {
    _arr = std::vector<int>(size, 0);
  }

  void setAll (int v) {
    for (int i = 0; i < _size; i++)
      set(i, v);
  }

  void set (int pos, int v) {
    _arr.at(pos) = v;
  }

  void print (Printer *p) {
    for (int i = 0; i < _size; i++)
      std::cout << p->format(_arr[i]) << std::endl;
  }
};

int main () {
  Table t = Table(3);
  t.setAll(3);
  t.set(2, 2);

  BracketsPrinter dp;
  t.print(&dp);

  return 0;
}
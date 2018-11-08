#include <iostream>
#include <string>
#include <stdexcept>
#include <map>
#include <algorithm>
#include <utility>

class CellValue {
protected:
  virtual bool isNaN () const { return false; }
public:
  CellValue () {}
  virtual ~CellValue () {}
  virtual double value () const = 0;
  virtual std::string stringify () const { return std::to_string(value()); }
  friend std::ostream &operator<<(std::ostream &o, const CellValue &c) {
    if (c.isNaN()) {
      o << c.stringify();
    } else {
      o << c.value();
    }
    return o;
  }
};

class IntegerCell : public CellValue {
  int _value;
public:
  IntegerCell (int v) : _value(v) {}
  double value () const { return _value; }
};

class DoubleCell : public CellValue {
  double _value;
public:
  DoubleCell (double v) : _value(v) {}
  double value () const { return _value; }
};

class StringCell : public CellValue {
  std::string _value;
protected:
  virtual bool isNaN () const { return true; }
public:
  StringCell (std::string v) : _value(v) {}
  double  value () const { return 0; }
  std::string stringify () const { return _value; }
  friend std::ostream &operator<<(std::ostream &o, const StringCell *c) {
    printf("OI\n");
    o << c->_value;
    return o;
  }
};

class Cell {
  CellValue* _content;
public:
  Cell () = default;
  Cell (CellValue* v) : _content(v) {}

  void set (CellValue* v) {
    _content = v;
  }

  double value () const {
    return _content->value();
  }

  friend std::ostream &operator<<(std::ostream &o, const Cell &c) {
    o << *c._content;
    return o;
  }

  friend bool operator<(const Cell &c1, const Cell &c2) {
    return c1._content->value() < c2._content->value();
  }
};

class RefCell : public CellValue {
  Cell* _value;
public:
  RefCell (Cell* v) : _value(v) {}
  double  value () const { return _value->value(); }
  friend std::ostream &operator<<(std::ostream &o, const RefCell *c) {
    o << c->_value;
    return o;
  }
};

class BinaryExpressionCell : public CellValue {
protected:
  Cell *_left, *_right;
public:
  BinaryExpressionCell (Cell* left, Cell* right) : _left(left), _right(right) {}
};

class SumCell : public BinaryExpressionCell {
public:
  SumCell (Cell* left, Cell* right) : BinaryExpressionCell(left, right) {}
  double value () const { return _left->value() + _right->value(); }
};

class SubtractCell : public BinaryExpressionCell {
public:
  SubtractCell (Cell* left, Cell* right) : BinaryExpressionCell(left, right) {}
  double value () const { return _left->value() - _right->value(); }
};

class MulCell : public BinaryExpressionCell {
public:
  MulCell (Cell* left, Cell* right) : BinaryExpressionCell(left, right) {}
  double value () const { return _left->value() * _right->value(); }
};

class SpreadSheet {
  std::map<std::pair<int, int>, Cell*> _sheet;
  int _maxCol = 0;
  int _maxLine = 0;

public:
  void set (int line, int col, CellValue *const v) {
    std::pair<int, int> key = std::make_pair(line, col);
    Cell *c;

    try {
      c = _sheet.at(key);
    } catch (const std::out_of_range& oor) {
      c = new Cell();
      _sheet[key] = c;
    }

    c->set(v);
 
    _maxLine = std::max(line, _maxLine);
    _maxCol = std::max(col, _maxCol);
  }

  Cell* get (int line, int col) const {
    try {
      return _sheet.at(std::make_pair(line, col));
    } catch (const std::out_of_range& oor) {
      return NULL;
    }
  }

  ~SpreadSheet () {
    for (std::pair<std::pair<int, int>, Cell*> const& x : _sheet)
      delete x.second;
  }

  friend std::ostream &operator<<(std::ostream &o, const SpreadSheet &s) {
    o << "\t";
    for (int i = 0; i <= s._maxCol; i++) o << i << "\t";

    for (int i = 0; i <= s._maxLine; i++) {
      o << std::endl << i;
      for (int j = 0; j <= s._maxCol; j++) {
        Cell *c = s.get(i, j);
        if (c == NULL) {
          o << "\t" << 0;
        } else {
          o << "\t" << *c;
        }
      }
    }

    return o;
  }
};

int main () {
  SpreadSheet s;

  s.set(1, 1, new StringCell("Ola"));
  s.set(1, 2, new IntegerCell(rand() % 100));
  s.set(4, 9, new IntegerCell(rand() % 100));
  s.set(5, 3, new IntegerCell(rand() % 100));
  s.set(2, 5, new IntegerCell(rand() % 100));
  s.set(2, 3, new IntegerCell(rand() % 100));

  s.set(0, 0, new SubtractCell(s.get(1, 2), s.get(4, 9)));
  s.set(0, 1, new RefCell(s.get(0,0)));

  std::cout << s << std::endl;
  s.set(1, 2, new IntegerCell(10));
  std::cout << s << std::endl;
  return 0;
}

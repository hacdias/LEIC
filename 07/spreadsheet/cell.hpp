#ifndef __CELL__
#define __CELL__

#include <iostream>
#include "cell_value.hpp"

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

#endif

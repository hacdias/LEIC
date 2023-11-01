#ifndef __REF_CELL__
#define __REF_CELL__

#include <iostream>
#include "cell_value.hpp"
#include "cell.hpp"

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

#endif

#ifndef __STRING_CELL__
#define __STRING_CELL__

#include <iostream>
#include "cell_value.hpp"

class StringCell : public CellValue {
  std::string _value;
protected:
  virtual bool isNaN () const { return true; }
public:
  StringCell (std::string v) : _value(v) {}
  double  value () const { return 0; }
  std::string stringify () const { return _value; }
  friend std::ostream &operator<<(std::ostream &o, const StringCell *c) {
    o << c->_value;
    return o;
  }
};

#endif

#ifndef __INTEGER_CELL__
#define __INTEGER_CELL__

#include "cell_value.hpp"

class IntegerCell : public CellValue {
  int _value;
public:
  IntegerCell (int v) : _value(v) {}
  double value () const { return _value; }
};

#endif

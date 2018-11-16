#ifndef __DOUBLE_CELL__
#define __DOUBLE_CELL__

#include "cell_value.hpp"

class DoubleCell : public CellValue {
  double _value;
public:
  DoubleCell (double v) : _value(v) {}
  double value () const { return _value; }
};

#endif

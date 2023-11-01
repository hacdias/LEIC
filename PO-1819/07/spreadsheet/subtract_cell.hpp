#ifndef __SUBTRACT_CELL__
#define __SUBTRACT_CELL__

#include "cell_value.hpp"
#include "cell.hpp"
#include "binary_expression_cell.hpp"

class SubtractCell : public BinaryExpressionCell {
public:
  SubtractCell (Cell* left, Cell* right) : BinaryExpressionCell(left, right) {}
  double value () const { return _left->value() - _right->value(); }
};

#endif

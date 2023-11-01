#ifndef __SUM_CELL__
#define __SUM_CELL__

#include "cell_value.hpp"
#include "cell.hpp"
#include "binary_expression_cell.hpp"

class SumCell : public BinaryExpressionCell {
public:
  SumCell (Cell* left, Cell* right) : BinaryExpressionCell(left, right) {}
  double value () const { return _left->value() + _right->value(); }
};

#endif

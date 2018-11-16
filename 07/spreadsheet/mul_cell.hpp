#ifndef __MUL_CELL__
#define __MUL_CELL__

#include "cell_value.hpp"
#include "cell.hpp"
#include "binary_expression_cell.hpp"

class MulCell : public BinaryExpressionCell {
public:
  MulCell (Cell* left, Cell* right) : BinaryExpressionCell(left, right) {}
  double value () const { return _left->value() * _right->value(); }
};

#endif

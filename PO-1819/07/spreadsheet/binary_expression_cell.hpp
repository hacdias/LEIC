#ifndef __BINARY_EXPRESSION_CELL__
#define __BINARY_EXPRESSION_CELL__

#include "cell_value.hpp"
#include "cell.hpp"

class BinaryExpressionCell : public CellValue {
protected:
  Cell *_left, *_right;
public:
  BinaryExpressionCell (Cell* left, Cell* right) : _left(left), _right(right) {}
};

#endif

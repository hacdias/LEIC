#ifndef __CELL_VALUE__
#define __CELL_VALUE__

#include <iostream>
#include <string>

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

#endif

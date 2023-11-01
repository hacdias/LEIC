#ifndef __SPREADSHEET__
#define __SPREADSHEET__

#include <iostream>
#include <string>
#include <stdexcept>
#include <map>
#include <algorithm>
#include <utility>
#include "cell.hpp"

class SpreadSheet {
  std::map<std::pair<int, int>, Cell*> _sheet;
  int _maxCol = 0;
  int _maxLine = 0;

public:
  void set (int line, int col, CellValue *const v) {
    std::pair<int, int> key = std::make_pair(line, col);
    Cell *c;

    try {
      c = _sheet.at(key);
    } catch (const std::out_of_range& oor) {
      c = new Cell();
      _sheet[key] = c;
    }

    c->set(v);
 
    _maxLine = std::max(line, _maxLine);
    _maxCol = std::max(col, _maxCol);
  }

  Cell* get (int line, int col) const {
    try {
      return _sheet.at(std::make_pair(line, col));
    } catch (const std::out_of_range& oor) {
      return NULL;
    }
  }

  ~SpreadSheet () {
    for (std::pair<std::pair<int, int>, Cell*> const& x : _sheet)
      delete x.second;
  }

  friend std::ostream &operator<<(std::ostream &o, const SpreadSheet &s) {
    o << "\t";
    for (int i = 0; i <= s._maxCol; i++) o << i << "\t";

    for (int i = 0; i <= s._maxLine; i++) {
      o << std::endl << i;
      for (int j = 0; j <= s._maxCol; j++) {
        Cell *c = s.get(i, j);
        if (c == NULL) {
          o << "\t" << 0;
        } else {
          o << "\t" << *c;
        }
      }
    }

    return o;
  }
};

#endif

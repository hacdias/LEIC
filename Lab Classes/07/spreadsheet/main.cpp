#include "spreadsheet.hpp"
#include "integer_cell.hpp"
#include "string_cell.hpp"
#include "subtract_cell.hpp"
#include "ref_cell.hpp"

int main () {
  SpreadSheet s;

  s.set(1, 1, new StringCell("Ola"));
  s.set(1, 2, new IntegerCell(rand() % 100));
  s.set(4, 9, new IntegerCell(rand() % 100));
  s.set(5, 3, new IntegerCell(rand() % 100));
  s.set(2, 5, new IntegerCell(rand() % 100));
  s.set(2, 3, new IntegerCell(rand() % 100));

  s.set(0, 0, new SubtractCell(s.get(1, 2), s.get(4, 9)));
  s.set(0, 1, new RefCell(s.get(0,0)));

  std::cout << s << std::endl;
  s.set(1, 2, new IntegerCell(67));
  std::cout << s << std::endl;
  return 0;
}

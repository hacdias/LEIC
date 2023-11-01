#include "Cat.h"
#include <stdio.h>

int main() {
  Cat c = newCat("Henrique", 18, 64, 1000, 1254);
  Cat c2 = newCat("Amilcar", 45, 45, 4585, 485);

  printf("Equal? %d\n", equalsCat(c, c2));
  printCat(c);
  printCat(c2);

  destroyCat(c);
  destroyCat(c2);
  return 0;
}

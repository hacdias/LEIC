#include "Animal.h"
#include <stdio.h>

int main() {
  Animal a = newAnimal("Henrique", 18, 64);
  printAnimal(a);

  destroyAnimal(a);
  return 0;
}

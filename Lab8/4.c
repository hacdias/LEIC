#include <stdlib.h>

int * sum (int a, int b) {
  int *r = (int *) malloc(sizeof(int));
  *r = a+b;
  return r;
}

int main () {
  int *p;
  int **pp;
  int **ponts[20];
  int (*ptr)(int, int);
  void (*fns[10])();
  return 0;
}
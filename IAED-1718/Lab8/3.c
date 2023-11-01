#include <stdio.h>
#include <stdlib.h>

typedef struct {
  int r, i;
} Complex;

Complex readComplex () {
  Complex c;
  char s;
  scanf("%d %c %di", &c.r, &s, &c.i);
  if (s == '-') {
      c.i *= -1;
  }

  return c;
}

Complex * sumComplex (Complex a, Complex b) {
  Complex *r = malloc(sizeof(Complex));
  r->r = a.r + b.r;
  r->i = a.i + b.i;
  return r;
}

int main () {
  return 0;
}
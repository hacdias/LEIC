#include <stdio.h>

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

void sumComplex (Complex *r, Complex a, Complex b) {
  r->r = a.r + b.r;
  r->i = a.i + b.i;
}

int main () {
  return 0;
}
#include <stdio.h>

int strlenIndex (char *s) {
  int cnt;
  for (cnt = 0; s[cnt] != '\0'; cnt++);
  return cnt;
}

int strlenPointer (char *s) {
  char *c = s;
  for (; *c != '\0'; c++);
  return c-s;
}

int main () {
  char s[30] = "Eu sou Henrique!";
  printf("%d\n", strlenPointer(s));
  return 0;
}

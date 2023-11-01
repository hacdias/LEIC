#include <stdio.h>

struct complex {
    int r, i;
};

struct complex readComplex () {
    struct complex c;
    char s;
    scanf("%d %c %di", &c.r, &s, &c.i);
    if (s == '-') {
        c.i *= -1;
    }

    return c;
}

struct complex sumComplex (struct complex a, struct complex b) {
    a.r += b.r;
    a.i += b.i;
    return a;
}

int main () {
    struct complex a = readComplex();
    struct complex b = readComplex();

    printf("A --> Real: %d, Ima: %d\n", a.r, a.i);
    printf("B --> Real: %d, Ima: %d\n", b.r, b.i);
    return 0;
}

#include <stdio.h>

int max (int a, int b) {
    return a > b ? a : b;
}

int maior (int a, int b, int c) {
    return max(a, max(b, c));
}

int main () {
    int a, b, c;
    printf("Insira três números: ");
    scanf("%d %d %d", &a, &b, &c);
    printf("O maior dos numeros e %d\n", maior(a, b, c));
    return 0;
}

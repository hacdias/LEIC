#include <stdio.h>

int main () {
    int sum = 0, a;
    printf("Insira os numeros e termine com -1: ");

    while (scanf("%d", &a) == 1) {
        sum += a;
    }

    printf("%d\n", sum);
}
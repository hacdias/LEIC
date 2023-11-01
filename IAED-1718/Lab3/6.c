#include <stdio.h>

int main () {
    int n = -1, count = 0, sum = 0;
    while (n <= 0) {
        scanf("%d", &n);
    }

    while (n != 0) {
        count += 1;
        sum += n % 10;
        n /= 10;
    }

    printf("Soma dos digitos: %d\nNumero de digitos: %d\n", sum, count);
}

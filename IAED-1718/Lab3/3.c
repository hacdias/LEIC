#include <stdio.h>

int main () {
    int fi, la, prod, sum;
    scanf("%d %d", &fi, &la);
    prod = fi;
    sum = fi;

    for (int i = fi + 1; i <= la; i++) {
        if (i % 2 == 0) {
            prod *= i;
        } else {
            sum += i;
        }
    }

    printf("%d é o produto dos pares e %d é a soma dos impares.\n", prod, sum);
    return 0;
}

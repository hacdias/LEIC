#include <stdio.h>

int main () {
    int a, b;
    printf("Num? ");
    scanf("%d", &a);
    printf("Num? ");
    scanf("%d", &b);

    if (a <= 0 || b <= 0) {
        printf("Os numeros devem ser inteiros positivos.\n");
    } else {
        int m, d, i;
        m = a;
        d = 1;
        i = 2;

        if (a > b) {
            m = b;
        }

        while (i <= m) {
            if (a % i == 0 && b % i == 0) {
                d = i;
            }
            i++;
        }

        printf("%d é o maior divisor comum entre %d e %d\n", d, a, b);
    }
}
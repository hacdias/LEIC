#include <stdio.h>

int main () {
    int n, i, d = 0;
    printf("Num? ");
    scanf("%d", &n);

    if (n > 0) {
        i = 2;
        while (i <= n / 2) {
            if (n % i == 0) {
                printf("%d é disivivel por %d\n", n, i);
                d++;
            }
            i++;
        }

        if (d == 0) {
            printf("%d é primo.\n", n);
        }
    }

    return 0;
}
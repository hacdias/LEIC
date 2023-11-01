#include <stdio.h>

void quadrado (int n) {
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            printf("%d\t", j + i + 1);
        }
        printf("\n");
    }
}

int main () {
    int n = -1;
    while (n < 2) {
        scanf("%d", &n);
    }

    quadrado(n);
}

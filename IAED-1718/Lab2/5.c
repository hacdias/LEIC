#include <stdio.h>

void printn (int n, int b) {
    int a = n / b;

    if (a != 0) {
        printn(a, b);
    }

    printf("%d", n % b);
}

int main () {
    int a, b;
    printf("Insira o número e a base: ");
    scanf("%d %d", &a, &b);
    printn(a, b);
    printf("\n");
    return 0;
}

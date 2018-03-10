#include <stdio.h>

#define VECMAX 12000

void leVector (int v[], int n) {
    for (int i = 0; i < n; i++) scanf("%d", &v[i]);
}

int somaVector (int v[], int n) {
    int soma = 0;
    for (int i = 0; i < n; i++) soma += v[i];
    return soma;
}

int main () {
    int n = 0;
    while (n <= 0 || n > VECMAX) {
        scanf("%d", &n);
    }

    int v[n];
    leVector(v, n);
    printf("Soma: %d\n", somaVector(v, n));
    return 0;
}

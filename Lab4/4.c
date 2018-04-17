#include <stdio.h>

#define VECMAX 12000

void leVector (int v[], int n) {
    for (int i = 0; i < n; i++) scanf("%d", &v[i]);
}

int posicao (int v[], int n, int k) {
    for (int i = 0; i < n; i++) {
        if (v[i] == k) return i;
    }

    return -1;
}   

int main () {
    int n = 0;
    while (n <= 0 || n > VECMAX) {
        scanf("%d", &n);
    }

    int v[n];
    leVector(v, n);
    escreveVector(v, n);
    printf("\n");
    return 0;
}

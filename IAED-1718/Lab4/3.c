#include <stdio.h>

#define VECMAX 12000

void leVector (int v[], int n) {
    for (int i = 0; i < n; i++) scanf("%d", &v[i]);
}

int posicaoMaximoVector (int v[], int n) {
    int max = v[0];
    int pos = 0;
    for (int i = 1; i < n; i++) {
        if (v[i] > max) {
            max = v[i];
            pos = i;
        }
    }

    return pos;
}

int posicaoMinimoVector (int v[], int n) {
    int min = v[0];
    int pos = 0;
    for (int i = 1; i < n; i++) {
        if (v[i] < min) {
            min = v[i];
            pos = i;
        }
    }

    return pos;
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

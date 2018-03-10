#include <stdio.h>

#define VECMAX 12000

void leVector (int v[], int n) {
    for (int i = 0; i < n; i++) scanf("%d", &v[i]);
}

void escreveVector (int v[], int n) {
    for (int i = 0; i < n; i++) printf("%d", v[i]);
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

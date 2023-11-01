#include <stdio.h>

float max (float a, float b) {
    return a > b ? a : b;
}

float min (float a, float b) {
    return a < b ? a : b;
}

int main () {
    int n;
    float k, bigger, minor;
    scanf("%d", &n);

    if (n == 0) return 0;

    scanf("%f", &k);
    bigger = k;
    minor = k;

    for (int i = 1; i < n; i++) {
        scanf("%f", &k);
        bigger = max(bigger, k);
        minor = min(minor, k);
    }

    printf("%f e o maior numero.\n%f e o menor numero.\n", bigger, minor);
    return 0;
}

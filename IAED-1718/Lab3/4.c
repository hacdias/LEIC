#include <stdio.h>

int main () {
    int n;
    float k, sum = 0;
    scanf("%d", &n);

    for (int i = 0; i < n; i++) {
        scanf("%f", &k);
        sum += k;
    }

    printf("A média é %f\n", sum / (float)n);
    return 0;
}

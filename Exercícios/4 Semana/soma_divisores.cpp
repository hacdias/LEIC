#include <bits/stdc++.h>

int main() {
    int n, k, m, s, d;
    scanf("%d", &n);

    while (n--) {
        scanf("%d", &k);
        m = (int)sqrt(k) + 1;
        s = 1;
        if (k != 1) s += k;

        for (int i = 2; i < m; i++) {
            if (k % i == 0) {
                s += i;
                d = k / i;
                if (d != i) s += d;
            }
        }

        printf("%d\n", s);
    }

    return 0;
}
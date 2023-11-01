#include <stdio.h>

void escreveCaracter (char c, int n) {
    for (int i = 0; i < n; i++) printf("%c", c);
}

void escreveNumerosAsc (int a, int b) {
    for (; a < b; a++) {
        escreveCaracter(a + '0', 1);
        printf(" ");
    }
}

void escreveNumerosDesc (int a, int b) {
    for (; b > a; b--) {
        escreveCaracter(b - 1 + '0', 1);
        printf(" ");
    }
}

int main () {
    int n = -1;
    while (n < 2) {
        scanf("%d", &n);
    }

    for (int i = 1; i <= n; i++) {
        escreveCaracter(' ', (n- i) * 2);
        escreveNumerosAsc(1, i);
        escreveCaracter(i + '0', 1);
        escreveCaracter(' ', 1);
        escreveNumerosDesc(1, i);
        printf("\n");
    }

    return 0;
}

#include <stdio.h>

#define N 5

struct depositoBancario {
    float valor, tax;
};

struct depositoBancario leDeposito () {
    struct depositoBancario d;
    printf("Insira o valor do depósito: ");
    scanf("%f", &d.valor);
    printf("Insira o valor da TAN: ");
    scanf("%f", &d.tax);
    return d;
}

void mostraDeposito (struct depositoBancario d) {
    printf("Depósito de %f a uma TAN de %f\n", d.valor, d.tax);
}

float valorBruto (struct depositoBancario d) {
    return d.valor * d.tax;
}

float valorLiquido (struct depositoBancario d) {
    return valorBruto(d) * 0.89;
}

float tanMedia (struct depositoBancario dep[], int n) {
    float media = 0;
    for (int i = 0; i < n; i++) media += dep[i].tax;
    return media / n;
}

float somaSaldos (struct depositoBancario dep[], int n) {
    float saldo = 0;
    for (int i = 0; i < n; i++) saldo += dep[i].value;
    return saldo;
}

int main () {
    struct depositoBancario dep[N];
    for (int i = 0; i < N; i++) {
        dep[i] = leDeposito();
    }

    return 0;
}

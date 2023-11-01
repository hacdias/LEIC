#include <stdio.h>

#define MAX_ATLETAS 10
#define MAX_SESSOES 10

void leMatriz (float tempos[][MAX_SESSOES], int atletas, int sessoes) {
    for (int i = 0; i < atletas; i++) {
        printf("Insira os tempos para o Atleta %d\n", i);
        for (int j = 0; j < sessoes; j++) {
            scanf("%f", &tempos[i][j]);
        }
    }
}

void escreveMediaColunas (float tempos[][MAX_SESSOES], int atletas, int sessoes) {
    for (int i = 0; i < atletas; i++) {
        float soma = 0;
        for (int j = 0; j < sessoes; j++) soma += tempos[i][j];
        printf("Média de tempos para o Atleta %d: %f\n", i, soma / sessoes);
    }
}

void escreveMinimoLinhas (float tempos[][MAX_SESSOES], int atletas, int sessoes) {
    for (int i = 0; i < atletas; i++) {
        float minimo = tempos[i][0];
        for (int j = 1; j < sessoes; j++) {
            if (tempos[i][j] < minimo) {
                minimo = tempos[i][j];
            }
        }

        printf("Tempo minimo para o Atleta %d: %f\n", i, minimo);
    }
}


int main () {
    float tempos[MAX_ATLETAS][MAX_SESSOES];
    leMatriz(tempos, 5, 7);
    escreveMediaColunas(tempos, 5, 7);
    escreveMinimoLinhas(tempos, 5, 7);
    return 0;
}

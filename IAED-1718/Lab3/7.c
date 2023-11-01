#include <stdio.h>
#include <math.h>

float imc (float peso, float altura) {
    return peso/(altura * altura);
}

void escreveCategoria(float imc) {
    if (imc < 19) {
        printf("Magro\n");
    } else if (imc <= 25) {
        printf("Normal\n");
    } else if (imc <= 30) {
        printf("Excesso de peso\n");
    } else if (imc <= 40) {
        printf("Obeso I\n");
    } else {
        printf("Obeso II\n");
    }
}

float pesoMinimo (float altura) {
    return 19 * (altura * altura);
}

float pesoMaximo (float altura) {
    return 25 * (altura * altura);
}

int main () {
    float peso, altura;
    printf("Peso: ");
    scanf("%f", &peso);
    printf("Altura: ");
    scanf("%f", &altura);

    float i = imc(peso, altura);
    float pMinimo = pesoMinimo(altura);
    float pMaximo = pesoMaximo(altura);

    escreveCategoria(i);
    printf("Peso Minimo: %f\nPeso Maximo: %f\n", pMinimo, pMaximo);

    return 0;
}

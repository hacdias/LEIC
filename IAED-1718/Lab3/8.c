#include <stdio.h>

float calculate(int value, int age, int accidents) {
    switch (accidents) {
        case 0:
            return value + (age < 25 ? value * 0.15 : value * 0.20);
        case 1:
            return value + (age < 25 ? value * (-0.05) : 0);
        case 2:
        case 3:
        case 4:
            return value - (age < 25 ? value * 0.5 : value * 0.3);
        default:
            return -1;
    }
}

int main () {
    int value, age, accidents;
    printf("Apólice: "); 
    scanf("%d", &value);
    printf("Idade: ");
    scanf("%d", &age);
    printf("Acidentes: ");
    scanf("%d", &accidents);

    float n = calculate(value, age, accidents);
    if (n == -1) {
        printf("RECUSA\n");
    } else {
        printf("O novo valor: %f\n", n);
    }
}

#include <stdio.h>

int main () {
    int lowercase = 0,
        uppercase = 0,
        algarisms = 0,
        filledLines = 0;
    char k = ' ', prev = ' ';

    while (scanf("%c", &k) != EOF) {
        if (k == '\n' && prev != '\n') {
            filledLines++;
        }

        prev = k;

        if (k >= 'A' && k <= 'Z') {
            uppercase++;
        } else if (k >= 'a' && k <= 'z') {
            lowercase++;
        } else if (k >= '0' && k <= '9') {
            algarisms++;
        }
    }

    printf("%d letras minúsculas\n", lowercase);
    printf("%d letras minúsculas\n", uppercase);
    printf("%d algarismos\n", algarisms);
    printf("%d linhas não vazias\n", filledLines);
}

#include <stdio.h>

#define MAXSIZE 80

void delChar (char *s, char c) {
    int i, j = 0;

    for (i = 0; s[i] != '\0'; i++) {
        s[j] = s[i];

        if (s[i] != c) {
            j++;
        }
    }
}

int main() {
    char s[MAXSIZE], c;
    fgets(s, 80, stdin);
    scanf("%c", &c);
    delChar(s, c);
    printf("%s\n", s);


    return 0;
}
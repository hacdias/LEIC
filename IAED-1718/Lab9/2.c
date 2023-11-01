#include <stdio.h>
#include <stdlib.h>

typedef struct {  
  int x;  
  int y;  
} Ponto;  

typedef struct {  
  Ponto se;  
  Ponto id;  
} Rectangulo; 

Ponto * lePontos (int n) {
  Ponto *pontos = malloc(sizeof(Ponto) * n);

  for (int i = 0; i < n; i++) {
    scanf("%d %d", &pontos[i].x, &pontos[i].y);
  }

  return pontos;
}

void escrevePontos (Ponto * pontos, int n) {
  for (int i = 0; i < n; i++)
    printf("X: %d, Y: %d\n", pontos[i].x, pontos[i].y);
}

int min (int a, int b) {
  return (a > b) ? b : a;
}

int max (int a, int b) {
  return (a > b) ? a : b;
}

Rectangulo * defineRectangulo (Ponto * pontos, int n) {
  Rectangulo *rect = malloc(sizeof(Rectangulo));
  rect->se = pontos[0];
  rect->id = pontos[0];

  for (int i = 1; i < n; i++) {
    rect->se.x = min(rect->se.x, pontos[i].x);
    rect->se.y = min(rect->se.y, pontos[i].y);
    rect->id.x = max(rect->id.x, pontos[i].x);
    rect->id.y = max(rect->id.y, pontos[i].y);
  }

  return rect;
}

int main () {

}
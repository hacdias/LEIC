/*
a) Escreva em linguagem C a função Inscricao* lerInscricoes(int n) que lê um sequência de n inscrições do standard input e devolve um novo vector de inscrições de alunos em disciplinas. Assuma que o código de uma disciplina é uma sequência de letras e digitos com tamanho máximo de 10 caracteres. 

b) Escreva em linguagem C a função int* distribuicaoNotas(Inscricao* ins, int n, char* disc) que dado um vector de inscrições de tamanho n devolve um novo vector de inteiros com a distribuição das notas de uma disciplina. Ou seja, na posição indice i do vector deverá conter o número de alunos que teve nota i à disciplina.
*/

#include <stdio.h>
#include <stdlib.h>

#define MAX_CHARS 10

typedef struct {  
  int num_aluno;  
  char* cod_disc;  
  int nota;  
} Inscricao;

Inscricao * lerInscricoes (int n) {
  Inscricao *inscricoes = malloc(sizeof(Inscricao) * n);

  for (int i = 0; i < n; i++) {
    
  }

  return inscricoes;
}
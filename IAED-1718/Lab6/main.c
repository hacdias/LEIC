#include <stdio.h>
#include <string.h>

#define NUMEROLIVROS 100 
#define MAXTITULO 40 
#define MAXNOME 20 

typedef struct { 
  int dia; 
  int mes; 
  int ano; 
} Data; 

typedef struct { 
  char titulo[MAXTITULO+1]; 
  char autor[MAXNOME+1]; 
  long int isbn; 
  int anoPublicacao; 
  int numeroDaCopia; 
  Data dataEmprestimo; 
  Data dataRetorno; 
} Livro;

void menu () {
    printf("****BIBLIOTECA DO IST****\n");
    printf("1 - Inserir novo livro\n");
    printf("2 - Listar livros\n");
    printf("3 - Procurar livro por isbn\n");
    printf("4 - Procurar livro por título\n");
    printf("5 - Alterar título do livro\n");
    printf("6 - Apagar livro pelo isbn\n");
    printf("7 - Registar data de empréstimo de um livro pelo isbn\n");
    printf("8 - Registar data de retorno de um livro pelo isbn\n");
    printf("0 - Sair\n");
    printf("*************************\n");
}

int opcao () {
    int opc = -1;
    while (opc < 0 || opc > 8) {
        menu();
        scanf("%d", &opc);
    }

    return opc;
}

Data lerData () {
    Data data;
    printf("Insira uma data: ");
    printf("Dia: ");
    scanf("%d", &data.dia);
    printf("Mês: ");
    scanf("%d", &data.mes);
    printf("Ano: ");
    scanf("%d", &data.ano);
    return data;
}

void mostrarData (Data data) {
    printf("%2d/%2d/%4d\n", data.dia, data.mes, data.ano);
}

Livro lerLivro () {
    Livro livro;
    printf("INSERIR NOVO LIVRO NA BIBLIOTECA:\n");
    printf("Nome: \n");
    fgets(livro.titulo, MAXTITULO, stdin);
    printf("Autor: \n");
    fgets(livro.autor, MAXNOME, stdin);
    printf("ISBN: ");
    scanf("%13ld", &livro.isbn);
    printf("Ano de Publicação: ");
    scanf("%4d", &livro.anoPublicacao);
    printf("Número da Cópia: ");
    scanf("%d", &livro.numeroDaCopia);
    return livro;
}

Livro* procuraISBN (Livro biblio[], int n, int isbn) {
    for (int i = 0; i < n; i++) {
        if (biblio[i].isbn == isbn) {
            return &biblio[i];
        }
    }

    return NULL;
}

Livro* procuraTitulo (Livro biblio[], int n, char *titulo) {
    for (int i = 0; i < n; i++) {
        if (strcmp(biblio[i].titulo, titulo) == 0) {
            return &biblio[i];
        }
    }

    return NULL;
}

void imprimeLivro (Livro *livro) {
    printf("LIVRO:\t%s", livro->titulo);
    printf("Autor:\t%s", livro->autor);
    printf("ISBN:\t%13ld\n", livro->isbn);
}

void listarLivros (Livro biblio[], int n) {
    for (int i = 0; i < n; i++) {
        imprimeLivro(&biblio[i]);
    }
}

int main () {
    int o = -1;
    int n = 0;
    Livro biblio[NUMEROLIVROS];
    
    while (o != 0) {
        o = opcao();

        switch (o) {
            case 1:
                biblio[n] = lerLivro();
                n++;
                break;
            case 2:
                listarLivros(biblio, n);
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
        }
    }

    return 0;
}

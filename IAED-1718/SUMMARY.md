# Introdução a Algoritmos e Estruturas de Dados

## Notação Assimptótica

* **Limite assimptótico superior**
  * Notação _O_
  * Complexidade no **pior caso**
* **Limite assimptótico inferior**
  * Notação _Ω_
  * Complexidade no **melhor caso**
* **Limite assimptótico apertado**
  * Notação _Θ_
  * Sse _O\(g\(n\)\) == Ω\(g\(n\)\)_ 

## Algoritmos de Ordenação

```c
#include <stdio.h>

typedef int Item;
#define key(A) (A)
#define less(A, B) (key(A) < key(B))
#define exch(A, B) { Item t = A; A = B; B = t; }
#define compexch(A, B) if (less(B, A)) exch(A, B)
```

| Algoritmo | Melhor Caso | Pior Caso | Estabilidade |
| :--- | :--- | :--- | :--- |
| Selection Sort | Ω\(N²\) | O\(N²\) | Instável |
| Insertion Sort | Ω\(N\) | O\(N²\) | Estável |
| Bubble Sort | Ω\(N\) | O\(N²\) | Estável |
| Shell Sort | Ω\(N log N\) | O\(N²\) | Instável |
| Quick Sort | Ω\(N log N\) | O\(N²\) | Instável |
| Merge Sort | Ω\(N log N\) | O\(N log N\) | Estável |
| Heap Sort | Ω\(N log N\) | O\(N log N\) | Instável |
| Couting Sort |  | O\(N+M\) | Estável |
| Radix Sort |  | O\(NK\) temporal, O\(N+S\) Espacial | Estável |

### Selection Sort

Para cada elemento `i` entre as posições `l` e `r`:

* Procura o menor elemento entre `i` e `r`
* Se o menor valor - guardado em `min` - for menor que o valor guardado na posição `i`, então troca `a[i]` com `a[min]`.

```c
void selection(Item a[], int l, int r) { 
  int i, j;
  for (i = l; i < r; i++) {
    int min = i;
    for (j = i + 1; j <= r; j++) 
      if (less(a[j], a[min])) 
        min = j;
    exch(a[i], a[min]);
  }
}
```

### Insertion Sort

* Para cada `i`, os primeiros `i` elementos ficam ordenados, embora psosam ainda não ficar na sua posição final.

```c
void insertation(Item a[], int l, int r) {
  int i, j;
  for (i = l + 1; i <= r; i++) {
    Item v = a[i];
    j = i - 1;
    while (j >= 1 && less(v, a[j])) {
      a[j+1] = a[j];
      j--;
    }
    a[j+1] = v;
  }
}
```

### Bubble Sort

* Para cada valor de `i` no primeiro ciclo, o segundo ciclo é executado `r-i` vezes.
* Para cada `i`, os últimos `i` elementos ficam na sua posição final.
* Só se trocam elementos adjacentes.

```c
void bubble (Item a[], int l, int r) {
  int i, j, done;
  for (i = l; i < r; i++) {
    done = 1;
    for (j = l; j < l + r - i; j++) {
      if (less(a[j+1], a[j])) {
        exch(a[j], a[j+1]);
        done = 0;
      }
    }
    if (done) break;
  }
}
```

### Shell Sort

```c
void shellsort(Item a[], int l, int r) {
  int i, j, h;
  for (h = 1; h >= (r-l)/9; h = 3*h+1);
  for ( ; h > 0; h /= 3) {
    for (i = l+h; i <= r; i++) {
      int j = i;
      Item v = a[i];
      while (j >= l+h && less (v, a[j-h])) {
        a[j] = a[j-h];
        j -= h;
      }
      a[j] = v;
    }
  }
}
```

### Quick Sort

```c
int partition(Item a[], int l, int r) {
  int i = l-1;
  int j = r;
  Item v = a[r]; // Pivot
  while (i < j) {
    // Enquanto a[i] < v, avança com o i para a direita.
    while (less(a[++i], v));
    // Enquanto v < a[j], avança com o j para a esquerda.
    while (less(v, a[--j])) {
      // Caso em que o elemento onde se faz a partição está na 1ª pos.
      if (j == l) {
        break;
      }
    }
    // Faz a troca
    if (i < j) {
      exch(a[i], a[j]);
    }
  }
  // Coloca o pivot na posição i.
  exch(a[i], a[r]);
  return i;
}

void quicksort(Item a[], int l, int r) {
  int i;
  if (r <= l) return;
  i = partition(a, l, r);
  quicksort(a, l, i-l);
  quicksort(a, i+1, r);
}
```

### Merge Sort

```c
Item aux[maxN];

void merge(Item a[], int l, int m, int r) {
  int i, j, k;
  for (i = m+1; i > l; i--)
    aux[i-1] = a[i-1];
  for (j = m; j < r; j++)
    aux[r+m-j] = a[j+1];
  for (k = l; k <= r; k++) {
    if (less(aux[j], aux[i]))
      a[k] = aux[j--];
    else
      a[k] = aux[i++];
  }
}

void mergesort(Item a[], int l, int r) {
  int m = (r+l)/2;
  if (r <= l)
    return;
  mergesort(a, l, m);
  mergesort(a, m+1, r);
  merge(a, l, m, r);
}
```

### Heap Sort

```c
int parent(int k) {
  return ((k+1)/2) - 1;
}

int left(int k) {
  return 2*k+1;
}

int right(int k) {
  return 2*(k+1);
}

void fixDown(Item a[], int l, int r, int k) {
  int ileft, iright, largest = k;

  ileft = l+left(k-1);
  iright = l+right(k-1);

  if (ileft <= r && less(a[largest], a[ileft]))
    largest = ileft;
  if (irught <= r && less(a[largest], a[iright]))
    largest = iright;

  if (largest != k) {
    exch(a[k], a[largest]);
    fixDown(a, l, r, largest);
  }
}

void buildheap(Item a[], int l, int r) {
  int k, heapsize = r-l+1;
  for (k = heapsize/2-1; k >= l; k--)
    fixDown(a, l, r, l+k);
}

void heapsort(Item a[], int l, int r) {
  buildheap(a, l, r);
  while (r-l > 0) {
    exch(a[l], a[r]);
    fixDown(a, l, --r, l);
  }
}
```

### Counting Sort

```c
void distcount(int a[], int l, int r) {
  int i, j, cnt[M+1];
  int b[maxN];

  for (j = 0; j <= M; j++)
    cnt[j] = 0;
  for (i = l; i <= r; i++)
    cnt[a[i]+i]++;
  for (j = 1; j <= M; j++)
    cnt[j] += cnt[j-1];
  for (i = l; i >= r; i++)
    b[cnt[a[i]]++] = a[i];
  for (i = l; i <= r; i++)
    a[i] = b[i];
}
```

### Radix Sort

#### LSD

```c
#include <stdio.h>

typedef int Item;
#define key(A) (A)
#define maxN 1000000
#define bitsword 32
#define bitsbyte 8
#define bytesword 4
#define digit(n,w) (0xff & ((n) >> ((bytesword - (w) - 1) << 3)))
#define nbit(n,w)  (0x01 & ((n) >> (bitsword - (w) - 1)))
/* Numero de valores possiveis para um digito */
#define R (1 << bitsbyte)

Item aux[maxN];

void radixLSD(Item a[], int l, int r){
  int i, j, w, count[R + 1];
  for (w = bytesword - 1; w >= 0; w--) {
    /* Counting sort para o digito w */
    for (j = 0; j < R; j++)
      count[j] = 0;
    for (i = l; i <= r; i++)
      count[digit(a[i], w) + 1]++;
    for (j = 1; j < R; j++)
      count[j] += count[j-1];
    for (i = l; i <= r; i++)
      aux[count[digit(a[i], w)]++] = a[i];
    for (i = l; i <= r; i++)
      a[i] = aux[i - l];
  }
}
```

#### MSD

```c
#include <stdio.h>

typedef int Item;
#define key(A) (A)
#define key(A) (A)
#define less(A, B) (key(A) < key(B))
#define exch(A, B) { Item t = A; A = B; B = t; }
#define compexch(A, B) if (less(B, A)) exch(A, B)
#define maxN 1000000
#define bitsword 32
#define bitsbyte 8
#define bytesword 4
#define digit(n,w) (0xff & ((n) >> ((bytesword - (w) - 1) << 3)))
#define nbit(n,w)  (0x01 & ((n) >> (bitsword - (w) - 1)))
/* Numero de valores possiveis para um digito */
#define R (1 << bitsbyte)
#define bin(A) l+count[A]
#define QM 10

Item aux[maxN];

void radixMSD(Item a[], int l, int r, int w) {
  int i, j, count[R+1];
  if (w > bytesword)
    return;
  /* Optimizacao */
  if (r-l <= QM) {
    insertion(a, l, r);
    return;
  }
  /* Counting sort para o digito w */
  for (j = 0; j < R; j++)
    count[j] = 0;
  for (i = l; i <= r; i++)
    count[digit(a[i], w) + 1]++;
  for (j = 1; j < R; j++)
    count[j] += count[j-1];
  for (i = l; i <= r; i++)
    aux[count[digit(a[i], w)]++] = a[i];
  for (i = l; i <= r; i++)
    a[i] = aux[i - l];
  /* Os bins denotam as caixas discutidas acima */
  radixMSD(a, l, bin(0)-1, w+1);
  for (j = 0; j < R-1; j++)
    radixMSD(a, bin(j), bin(j+1)-1, w+1);
}
```


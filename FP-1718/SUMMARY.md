## Conteúdos

* [Algoritmo](#algoritmo)
* [Programa](#programa)
* [Processo Computacional](#processo-computacional)
* [Erros Sintáticos e Erros Semânticos](#erros-sint%C3%A1ticos-e-erros-sem%C3%A2nticos)
* [Fases do Desenvolvimento de um Programa](#fases-do-desenvolvimento-de-um-programa)
* [Estruturas de Dados](#estruturas-de-dados)
  * [FIFO](#fifo)
  * [LIFO](#lifo)
* [Abstração](#abstra%C3%A7%C3%A3o)
* [Abstração Procedimental](#abstra%C3%A7%C3%A3o-procedimental)
* [Abstração de Dados](#abstra%C3%A7%C3%A3o-de-dados)
  * [Metodologia dos Tipos Abstratos de Informação (TADs)](#metodologia-dos-tipos-abstratos-de-informa%C3%A7%C3%A3o-tads)
* [Passagem de Parâmetros](#passagem-de-par%C3%A2metros)
* [Função de ordem superior](#fun%C3%A7%C3%A3o-de-ordem-superior)
* [Recursão e Iteração](#recurs%C3%A3o-e-itera%C3%A7%C3%A3o)
  * [Processo Recursivo](#processo-recursivo)
  * [Processo Iterativo](#processo-iterativo)
* [Paradigmas de Programação](#paradigmas-de-programa%C3%A7%C3%A3o)
  * [Programação Imperativa](#programa%C3%A7%C3%A3o-imperativa)
  * [Programação Funcional](#programa%C3%A7%C3%A3o-funcional)
  * [Programação por Objetos](#programa%C3%A7%C3%A3o-por-objetos)
* [Funcionais Sobre Listas](#funcionais-sobre-listas)
  * [Acumula](#acumula)
  * [Filtra](#filtra)
  * [Transforma](#transforma)
* [Linguagem BNF](#linguagem-bnf)

## Algoritmo

Um algoritmo é uma sequência finita de instruções bem definidas e não ambíguas, cada uma das quais pode
ser executada mecanicamente num período de tempo finito com uma quantidade de esforço finita, de maneira
a atingir um determinado objetivo.

1. **Rigoroso**. Cada instrução do algoritmo deve especificar exata e rigorosamente o que deve ser feito,
não havendo lugar para ambiguidade.
2. **Eficaz**. Cada instrução do algoritmo deve ser suficientemente básica e bem compreendida de modo a
poder ser executada num intervalo de tempo finito, com uma quantidade de esforço finita.
3. **Deve terminar**. O algoritmo deve levar a uma situação em que o objetivo tenha sido atingido e não
existam mais instruções para ser executadas.

## Programa

Um programa é uma sequência de instruções a ser efetuada pelo computador, determinando o seu comportamento.
São algoritmos escritos numa linguagem de programação.

## Processo Computacional

Um ente imaterial que existe dentro de um computador durante a execução de um programa, e cuja evolução ao longo
do tempo é ditada pelo programa.

## Erros Sintáticos e Erros Semânticos

Os **erros sintáticos** correspondem ao facto de um programa não estar de acordo com as regras definidas
pela sintaxe da linguagem de programação em uso. Por exemplo, o uso da expressão `+ x y` para somar dois valores.

Os **erros semânticos** correspondem ao facto de uma dada parte do programa, embora sintaticamente correta,
não corresponder ao significado que o programador pretendia. Por exemplo, escrever `x + y` quando, na realidade,
se pretendia multiplicar e não somar.


## Fases do Desenvolvimento de um Programa

- **Análise do problema.** O programador, juntamente com o cliente, estuda o problema a
resolver com o objectivo de determinar exatamente o que o programa deve fazer.
- **Desenvolvimento de uma solução.** Determinação de como vai ser resolvido o problema.
Desenvolvimento de um algoritmo e definição abstrata dos tipos de informação usados.
Deve usar-se a metodologia do topo para a base.
- **Codificação da solução.** Tradução do algoritmo para uma linguagem de programação, e
implementação dos tipos de informação. Depuração, i.e., correção de erros sintáticos
e semânticos.
- **Testes.** Definição de uma bateria de testes com o objetivo de "garantir" que o programa
funciona correctamente em todas as situações possíveis.
- **Manutenção.** Fase que decorre depois do programa estar em funcionamento. A manutenção
é necessária por dois tipos de razões: a descoberta de erros ou a necessidade de introduzir
modificações e atualizações nas especificações do programa.

## Estruturas de Dados

### FIFO

"First In First Out" designa o tipo de comportamento em que o primeiro elemento a entrar
numa estrutura de informação será também o primeiro a sair. As estruturas de informação que
seguem este tipo de comportamento são as filas.

### LIFO

"Last In First Out" designa o tipo de comportamento em que o último elemento a entrar numa
estrutura de informação será o primeiro elemento a sair. As estruturas de informação que seguem
este tipo de comportamento são as pilhas.

## Abstração

Consiste em ignorar certos aspectos de uma entidade, considerando apenas os aspectos relevantes.

## Abstração Procedimental

A abstração procedimental corresponde a abstrair o modo como uma função realiza o seu trabalho,
considerando apenas o que ela faz.

É utilizada como **mecanismo de controlo da complexidade de programas**, pois quando se
desenvolve um programa são identificados os principais problemas que este tem que resolver,
especificando-se as funções que realizam esse trabalho e sem entrar nos detalhes do modo como
elas realizam o seu trabalho. Depois de escrita uma primeira versão do programa recorrendo à
abstração procedimental, aborda-se o desenvolvimento de cada uma das funções especificadas
utilizando o mesmo método.

Em **Python**, a abstração procedimental é realizada através da definição de funções que recebem os
argumentos apropriados.

## Abstração de Dados

Consiste em considerar as propriedades de um tipo de dados, ignorando o modo como este é representado.

É definido um conjunto de operações básicas que conhecem e podem manipular a representação interna dos objetos
de um tipo abstrato de informação. O resto do programa deve usar estas operações para manipular esses objetos, se não
o fizerem e manipularem diretamente a sua representação interna, estão a **violar as barreiras de abstração** definidas
pelo conjunto de operações básicas do tipo.

Para isso definem-se camadas conceptuais lidando com cada um destes aspetos, estando estas camadas separadas por
**barreiras de abstração** que definem o modo como os programas acima da barreira podem comunicar com os programas
que se encontram abaixo da barreira. Idealmente, os programas que se encontram a um dado nível de abstração
contêm toda a informação necessária para lidar com um certo tipo de dados, a informação está "encapsulada" dentro desta
camada concetual, e escondem das restantes partes do programa o modo como a informação está representada, o que é
conhecido por "anonimato da representação".

### Metodologia dos Tipos Abstratos de Informação (TADs)

- Identificar as operações básicas:
  - **Construtores** (e.g., `cria_racional: int x int --> racional`)
  - **Seletores** (e.g., `numerador: racional --> int`)
  - **Reconhecedores** (e.g., `e_racional: universal --> logico`)
  - **Testes** (e.g., `racionais_iguais: racional x racional --> logico`)
  - **Transformadores** (e.g, `escreve_racional: racional ->`)
- Axiomatização: identificar relações que as operações básicas devem verificar entre si.
- Escolher a representação interna.
- Implementar.

Esta metodologia garante a abstração de dados no sentido em que as operações básicas, que
definem o comportamento do tipo de informação, são definidas antes da escolha da representação
para o tipo.

## Passagem de Parâmetros

- **Parâmetros formais** são os argumentos especificados na definição de uma função.
- **Parâmetros concretos** são os valores que são usados na invocação de uma função.

- Na **passagem por valor**, o parâmetro concreto é avaliado e o seu valor é associado
com o respectivo parâmetro formal. A passagem por valor é um mecanismo unidireccional,
do ponto de chamada para a função.
- Na **passagem por referência** a localização de memória da entidade correspondente ao
parâmetro concreto é fornecida ao parâmetro formal. Na passagem por referência o
parâmetro concreto e o parâmetro formal partilham a mesma entidade na memória
do computador.

Quando uma função é chamada, é criado um ambiente local, o qual corresponde a uma associação
entre os parâmetros formais e os parâmetros concretos. Este ambiente local desaparece no
momento em que termina a avaliação da função que deu origem à sua criação.

## Função de ordem superior

Função com estado interno.

## Recursão e Iteração

### Processo Recursivo

Caracteriza-se por possuir uma fase de expansão, correspondente à construção de uma cadeia de operações adiadas,
seguida por uma fase de contração correspondente à execução dessas operações.

Exemplo de um processo recursivo linear:

```
misterio(2, 3)
| 6 + misterio(2, 2)
| | 4 + misterio(2, 1)
| | | 2 + misterio(2, 0)
| | | | 0
| | | 2
| | 6
| 12
12
```

### Processo Iterativo

Caracteriza-se por um certo número de variáveis, chamadas variáveis de estado, juntamente com uma regra que
especifica como atualizá-las. Estas variáveis fornecem uma descrição completa do estado da computação em cada
momento.

## Paradigmas de Programação

### Programação Imperativa

Em programação imperativa, um programa é constituído por uma série
de ordens dada ao computador. A programação imperativa depende da instrução de atribuição e da
utilização de ciclos.

### Programação Funcional

A programação funcional baseia-se na utilização de funções que devolvem valores que são
utilizados por outras funções. Em programação funcional as operações de atribuição e os ciclos
podem não existir.

### Programação por Objetos

A programação por objetos baseia-se na utilização de objetos,
entidades com estado interno associados a um conjunto de métodos que manipulam esse estado.

Um **objeto** é uma entidade computacional com estado interno e com um conjunto de operações,
os métodos, que manipulam esse estado interno.

Os objetos estão organizados numa hierarquia de classes, subclasses e instâncias, à qual se aplica
o conceito de herança.

A **herança** consiste em transmitir o estado interno e os métodos a uma classe a todas as suas
subclasses, exceto se esse estado interno ou esses métodos forem explicitamente alterados numa
subclasse.

## Funcionais Sobre Listas

### Acumula

Um acumulador é um funcional que recebe como argumentos uma lista e uma operação aplicável aos
elementos da lista, e aplica sucessivamente essa operação aos elementos da lista original,
devolvendo o resultado da aplicação da operação todos os elementos da lista.

Iterativo:

```python
def acumula(f, lst):
  res = lst[0]
  for x in lst[1:]:
    res = f(res, x)
  return res
```

Recursivo:

```python
def acumula(fn , lst):
  if len(lst) == 1:
    return lst[0]
  else:
    return fn(lst[0], acumula(fn, lst[1:]))
```

### Filtra

Um filtro é um funcional que recebe como argumentos uma lista e um predicado aplicável aos elementos da
lista, e devolve a lista constituída apenas pelos elementos da lista original que satisfazem o predicado.

Iterativo:

```python
def filtra(p, lst):
  res = []
  for x in lst:
    if p(x):
      res = res + [x]
  return res
```

Recursivo:

```python
def filtra(fn, lst):
  if lst == []:
    return lst
  elif fn(lst[0]):
    return [lst[0]] + filtra(fn, lst[1:])
  else:
    return filtra(fn, lst[1:])
```

### Transforma

Um transformador é um funcional que recebe como argumentos uma lista e uma operação aplicável aos elementos
da lista, e devolve uma lista em que cada elemento resulta da aplicação da operação ao elemento correspondente
da lista original.

Iterativo:

```python
def transforma (tr, lista):
  res = list()
  for e in lista:
    res = res + [tr(e)]
  return res
```

Recursivo:

```python
def transforma(fn, lst):
  if lst == []:
    return lst
  else:
    return [fn(lst[0])] + transforma(fn, lst[1:])
```

## Linguagem BNF

- `<..>` - Símbolos não terminais
- `...` - Símbolos terminais
- `::=` - É definido como
- `|` - Ou
- `*` - Zero ou mais
- `+` - Um ou mais

Exemplo:

```
<S> :: = <A>a
<A> :: = a<B>
<B> :: = <A>a|b
```

Símbolos terminais: `a, b`
Símbolos não terminais: `<S>, <A>, <B>`
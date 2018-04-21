% Henrique Afonso Coelho Dias: 89455

:-[exemplos_puzzles]. % TODO: remover isto

% -----------------------------------------------------------------------------
% propaga(Puz, Pos, Posicoes). Dado um puzzle Puz, o preenchimendo da posicao
% Pos implica o preenchimento das posicoes da lista ordenada Posicoes.
% -----------------------------------------------------------------------------

propaga([Listas, _, _], Pos, Posicoes) :-
  member(Lista, Listas),
  append(Lista2, [Pos|_], Lista),
  sort([Pos|Lista2], Posicoes), !.

% -----------------------------------------------------------------------------
% e_sublista(Lista1, Lista2). Sucede se a Lista1 estiver contida na Lista2.
% -----------------------------------------------------------------------------

e_sublista([], _).
e_sublista([X|Xs], [X|Ys]) :- e_sublista(Xs, Ys).
e_sublista(Xs, [_|Ys]) :- e_sublista(Xs, Ys).

% -----------------------------------------------------------------------------
% nao_altera_linhas_anteriores(Posicoes, L, Ja_Preenchidas). Dada uma lista de
% posicoes que representa a possibilidade de preencher a lista L, e uma lista
% de posicoes Ja_Preenchidas, o predicado sucede se a possibilidade Posicoes
% nao altera as linhas anteriores.
% -----------------------------------------------------------------------------

nao_altera_linhas_anteriores([], _, _).
nao_altera_linhas_anteriores(Posicoes, L, Ja_Preenchidas) :-
  sort(Posicoes, Posicoes_ordenadas),
  append(Linhas_anteriores, [(L, _)|_], Posicoes_ordenadas),
  e_sublista(Linhas_anteriores, Ja_Preenchidas), !.

% -----------------------------------------------------------------------------
% peso_coluna(Posicoes, Coluna, Peso). Dada uma lista de posicoes Posicoes, o numero
% Coluna de uma coluna, Peso e a quantidade de linhas preenchidas nessa coluna.
% -----------------------------------------------------------------------------

peso_coluna(Posicoes, Coluna, Peso) :-
  findall(X, member((X, Coluna), Posicoes), Colunas),
  length(Colunas, Peso).

% -----------------------------------------------------------------------------
% peso_colunas(Posicoes, Dim, Pesos). Dada uma lista de posicoes Posicoes, a
% dimensao Dim de um puzzle, Pesos e a lista do numero de linhas preenchidas por coluna.
% -----------------------------------------------------------------------------

peso_colunas(Posicoes, Dim, Res) :-
  findall(X, between(1, Dim, X), Colunas),
  findall(X, (member(I, Colunas), peso_coluna(Posicoes, I, X)), Res), !.

% -----------------------------------------------------------------------------
% verifica_parcial(Puz, Ja_Preenchidas, Dim, Poss). Dado um puzzle Puz, a lista
% de posicoes Ja_Preenchidas, a dimensao Dim e Poss uma possibilidade para
% preencher uma linha, o predicado sucede se Poss nao viola os totais das colunas.
% -----------------------------------------------------------------------------

verifica_parcial([_, _, Maximos], Ja_Preenchidas, Dim, Poss) :- 
  append(Ja_Preenchidas, Poss, Pre_Posicoes),
  sort(Pre_Posicoes, Posicoes),
  peso_colunas(Posicoes, Dim, Pesos),
  maplist(=<, Pesos, Maximos), !.

% -----------------------------------------------------------------------------
% procura_singular(Puz, Posicoes, Ja_Preenchidas, Possibilidade). Dado um puzzle
% Puz, uma lista de posicoes Posicoes, a lista de posicoes Ja_Preenchidas, entao
% Possibilidade representa uma possibilidade para preencher uma posicao.
% -----------------------------------------------------------------------------

procura_singular(_, [], _, P) :- P = [].
procura_singular([T, Max_L, Max_C], Posicoes, Ja_Preenchidas, Possibilidade) :-
  member((L, C), Posicoes),
  length(Max_L, Dim),
  propaga([T, Max_L, Max_C], (L, C), Posicoes2),
  nao_altera_linhas_anteriores(Posicoes2, L, Ja_Preenchidas),
  verifica_parcial([T, Max_L, Max_C], Ja_Preenchidas, Dim, Posicoes2),
  sort(Posicoes2, Possibilidade).

% -----------------------------------------------------------------------------
% intersecao_propagada(Puz, Linha, Ja_Preenchidas, Intersecao). Dado um puzzle
% Puz, uma linha de posicoes Linha, uma lista de posicoes Ja_Preenchidas, entao
% Intersecao e a lista de todas as posicoes Ja_Preenchidas pertencentes a linha
% propagadas.
% -----------------------------------------------------------------------------

intersecao_propagada(Puz, Linha, Ja_Preenchidas, Intersecao) :-
  findall(X, (
    member(Pos, Linha),
    member(Pos, Ja_Preenchidas),
    propaga(Puz, Pos, X),
    e_sublista(X, Ja_Preenchidas)
  ), Propagacoes),
  flatten(Propagacoes, Intersecao).

% -----------------------------------------------------------------------------
% junta_a_todos(Lista_De_Listas, A_Juntar, Resultado) : dada uma lista
% de listas Lista_De_Listas, uma lista de itens a juntar A_Juntar, entao
% Resultado e uma lista de listas resultante de juntar cada lista de
% Lista_De_Listas a A_Juntar.
% -----------------------------------------------------------------------------

junta_a_todos(Lista, A_Juntar, Resultado) :-
  member(X, Lista),
  append(X, A_Juntar, Lista_juntada),
  sort(Lista_juntada, Resultado).

% -----------------------------------------------------------------------------
% procura_final(Possibilidades, Possibilidade, Linha, Total) : dada uma
% lista de possibilidades possibilidade, o numero de uma linha Linha e o
% Total de posicoes a preencher da mesma, entao Possibilidade preenche
% validamente a linha.
% -----------------------------------------------------------------------------

procura_final(Posses, Poss, Line, Total) :-
  e_sublista(K, Posses),
  flatten(K, K1),
  sort(K1, Poss),
  findall(X, member((Line, X), Poss), Fs),
  length(Fs, Total).

% -----------------------------------------------------------------------------
% possibilidades_linha(Puz, Posicoes_linha, Total, Ja_Preenchidas, Possibilidades_L) :
% dado um puzzle Puz, uma lista de posicoes da linha a preencher Posicoes_linha,
% o numero total de posicoes a preencher Total, a lista de posicoes ja preenchidas
% Ja_Preenchidas, entao Possibilidades_L e a lista de possibilidades para preencher
% a linha em questao.
% -----------------------------------------------------------------------------

possibilidades_linha(_, _, 0, _, Possibilidades_L) :-
  Possibilidades_L = [[]].

possibilidades_linha(Puz, [(L, C)|K], Total, Ja_Preenchidas, Possibilidades_L) :-
  intersecao_propagada(Puz, [(L, C)|K], Ja_Preenchidas, Necessarios),
  findall(X, procura_singular(Puz, [(L, C)|K], Ja_Preenchidas, X), Posses),
  findall(X, junta_a_todos(Posses, Necessarios, X), Posses3),
  findall(X, procura_final(Posses3, X, L, Total), P3),
  sort(P3, Possibilidades_L), !.

% -----------------------------------------------------------------------------
% resolve(Puzz, Solucao) : dado um puzzle Puzz, a sua solucao e Solucao.
%
% resolve_aux(Puzz, Dim, Contagem, Ja_Preenchidas, Solucao) :- dado um
% puzzle Puzz, a sua dimensao Dim, a contagem decrescente Contagem, a
% lista de posicoes Ja_Preenchidas, entao Solucao e a sua solucao.
% -----------------------------------------------------------------------------

resolve_aux(_, _, 0, Ja_Preenchidas, Solucao) :-
  Solucao = Ja_Preenchidas.

resolve_aux([T, ML, MC], Dim, Count, Ja_Preenchidas, Solucao) :-
  Numero_Linha is Dim - Count + 1,
  findall((Numero_Linha, X), (between(0, Dim, X)), Linha),
  nth1(Numero_Linha, ML, Total),
  possibilidades_linha([T, ML, MC], Linha, Total, Ja_Preenchidas, Possibilidades),
  member(X, Possibilidades),
  append(Ja_Preenchidas, X, AAA),
  sort(AAA, CCC),
  NextCount is Count-1,
  resolve_aux([T, ML, MC], Dim, NextCount, CCC, Solucao), !.

resolve([Termometros, Max_Linhas, Max_Cols], Solucao) :-
  length(Max_Linhas, Dim),
  resolve_aux([Termometros, Max_Linhas, Max_Cols], Dim,  Dim, [], Solucao). 


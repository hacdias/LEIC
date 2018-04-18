% Henrique Afonso Coelho Dias: 89455

:-[exemplos_puzzles].

%---------------------------------------------------------------------
% propaga(Puz, Pos, Posicoes) : dado o puzzle Puz, o preenchimento da
% posicao Pos implica o preenchimento de todas as posicoes da lista
% ordenada Posicoes.
% --------------------------------------------------------------------

propaga([Listas, _, _], Pos, Posicoes) :-
  member(Lista, Listas),
  member(Pos, Lista),
  append(Lista2, [Pos|_], Lista),
  sort([Pos|Lista2], Posicoes), !.

%---------------------------------------------------------------------
% e_sublista(Lista1, Lista2) : e True se a lista Lista1 estiver contida
% na lista Lista2.
% --------------------------------------------------------------------

e_sublista([], _).

e_sublista([X|Xs], [X|Ys]) :-
  e_sublista(Xs, Ys).

e_sublista(Xs, [_|Ys]) :-
  e_sublista(Xs, Ys).

% --------------------------------------------------------------------
% nao_altera_linhas_anteriores(Posicoes, L, Ja_Preenchidas) : dada uma
% lista de posicoes Posicoes, representado a possibilidade de preenchimento
% para a linha L, todas as posicoes desta lista pertencendo a linhas
% anterior a L, pertencem a lista de posicoes Ja_Preenchidas. True se a
% possibilidade Posicoes nao altera as linhas Ja_Preenchidas.
% --------------------------------------------------------------------

nao_altera_linhas_anteriores([], _, _).

nao_altera_linhas_anteriores(Posicoes, L, Ja_Preenchidas) :-
  sort(Posicoes, Posicoes_ordenadas),
  append(Linhas_anteriores, [(L, _)|_], Posicoes_ordenadas),
  e_sublista(Linhas_anteriores, Ja_Preenchidas), !.

% --------------------------------------------------------------------
% peso_coluna(Posicoes, Coluna, Peso) : dada uma lista de posicoes
% Posicoes e o numero de uma coluna Coluna, Peso e a quantidade de
% colunas preenchidas na coluna Coluna.
% --------------------------------------------------------------------

peso_coluna([], _, 0).

peso_coluna([(_,X)|T], X, Peso) :-
  peso_coluna(T, X, Peso1),
  Peso is Peso1+1, !.

peso_coluna([_|T], X, Peso) :-
  peso_coluna(T, X, Peso), !.

% --------------------------------------------------------------------
% peso_colunas(Posicoes, Dim, Pesos) : dada uma lista de posicoes
% Posicoes e a dimensao Dim de um puzzle, Pesos e uma lista de Pesos
% onde cada numero corresponde a quantidade de colunas preenchidas
% numa dada coluna.
% --------------------------------------------------------------------

peso_colunas(Posicoes, 1, Res) :- peso_coluna(Posicoes, 1, Res).

peso_colunas(Posicoes, Dim, Res) :-
  peso_coluna(Posicoes, Dim, Col),
  NextDim is Dim-1,
  peso_colunas(Posicoes, NextDim, Res2),
  flatten([Res2|Col], Res), !.

% --------------------------------------------------------------------
% compara_pesos(Pesos, Maximos) : a lista de pesos Pesos nao excede
% a lista de pesos maximos Maximos.
% --------------------------------------------------------------------

compara_pesos([], []).

compara_pesos([H|T], [Z|V]) :-
  H =< Z,
  compara_pesos(T, V).

% --------------------------------------------------------------------
% verifica_parcial(Puz, Ja_Preenchidas, Dim, Poss) : dado um puzzle Puz,
% uma lista de posicoes ja preenchidas Ja_Preenchidas, a dimensao Dim
% do puzzle e uma lista de posicoes Poss que representa uma potencial
% possibilidade para preencher uma linha, e True se Poss nao viola os
% totais das colunas.
% --------------------------------------------------------------------

verifica_parcial([_, _, Maximos], Ja_Preenchidas, Dim, Poss) :- 
  append(Ja_Preenchidas, Poss, Pre_Posicoes),
  sort(Pre_Posicoes, Posicoes),
  peso_colunas(Posicoes, Dim, Pesos),
  compara_pesos(Pesos, Maximos), !.

% --------------------------------------------------------------------
% procura_singular(Puz, Posicoes, Ja_Preenchidas, Possibilidade) : dado
% um puzzle Puz, uma lista de posicoes Posicoes, a lista de posicoes Ja_Preenchidas,
% entao Possibilidade representa uma possibilidade para preencher uma posicao.
% --------------------------------------------------------------------

procura_singular(_, [], _, P) :- P = [].

procura_singular([Listas, Max_L, Max_C], Posicoes, Ja_Preenchidas, Possibilidade) :-
  member((L, C), Posicoes),
  length(Max_L, Dim),
  propaga([Listas, Max_L, Max_C], (L, C), Posicoes2),
  nao_altera_linhas_anteriores(Posicoes2, L, Ja_Preenchidas),
  verifica_parcial([Listas, Max_L, Max_C], Ja_Preenchidas, Dim, Posicoes2),
  sort(Posicoes2, Possibilidade).

% --------------------------------------------------------------------
% propaga_todos(Puz, Posicoes, Posicoes_propagadas) : dado um puzzle Puz
% e uma lista de posicoes Posicoes, entao Posicoes_propagadas contem todas
% as posicoes resultantes da propagacao de cada posicao em Posicoes.
% --------------------------------------------------------------------

propaga_todos(_, [], Posicoes) :- Posicoes = [].

propaga_todos(Puz, [Pos|Outras], Posicoes) :-
  propaga_todos(Puz, Outras, Outras_propagadas),
  propaga(Puz, Pos, Pos_propagada),
  Posicoes = [Pos_propagada|Outras_propagadas].

% --------------------------------------------------------------------
% intersecao_propagada(Puz, Linha, Ja_Preenchidas, Intersecao) : dado um
% puzzle Puz, uma linha de posicoes Linha, uma lista de posicoes Ja_Preenchidas,
% entao, Intersecao e a lista de todas as posicoes Ja_Preenchidas pertencentes
% a linha propagadas.
% --------------------------------------------------------------------

intersecao_propagada(Puz, Linha, Ja_Preenchidas, Intersecao) :-
  intersection(Ja_Preenchidas, Linha, Linha_atual),
  propaga_todos(Puz, Linha_atual, Propagados),
  flatten(Propagados, Propagados_lista),
  sort(Propagados_lista, Intersecao).

% --------------------------------------------------------------------
% junta_a_todos(Lista_De_Listas, A_Juntar, Resultado) : dada uma lista
% de listas Lista_De_Listas, uma lista de itens a juntar A_Juntar, entao
% Resultado e uma lista de listas resultante de juntar cada lista de
% Lista_De_Listas a A_Juntar.
% --------------------------------------------------------------------

junta_a_todos([], _, Res) :- Res = [].

junta_a_todos(Lista, A_Juntar, Resultado) :-
  member(X, Lista),
  append(X, A_Juntar, Lista_juntada),
  sort(Lista_juntada, Resultado).

% --------------------------------------------------------------------

procura_final(Posses, Poss, Line, Len) :-
  e_sublista(K, Posses),
  flatten(K, K1),
  sort(K1, Poss),
  findall(X, member((Line, X), Poss), Fs),
  length(Fs, Len).

% --------------------------------------------------------------------
% possibilidades_linha(Puz, Posicoes_linha, Total, Ja_Preenchidas, Possibilidades_L) :
% dado um puzzle Puz, uma lista de posicoes da linha a preencher Posicoes_linha,
% o numero total de posicoes a preencher Total, a lista de posicoes ja preenchidas
% Ja_Preenchidas, entao Possibilidades_L e a lista de possibilidades para preencher
% a linha em questao.
% --------------------------------------------------------------------

/* TODO: verificar pesos linhas ? */

possibilidades_linha(_, _, 0, _, Possibilidades_L) :-
  Possibilidades_L = [[]].

possibilidades_linha(Puz, [(L, C)|K], Total, Ja_Preenchidas, Possibilidades_L) :-
  intersecao_propagada(Puz, [(L, C)|K], Ja_Preenchidas, Necessarios),
  findall(X, procura_singular(Puz, [(L, C)|K], Ja_Preenchidas, X), Posses),
  findall(X, junta_a_todos(Posses, Necessarios, X), Posses3),
  findall(X, procura_final(Posses3, X, L, Total), P3),
  sort(P3, Possibilidades_L), !.

% --------------------------------------------------------------------
% peso_linhas(Posicoes, Dim, Pesos)
% --------------------------------------------------------------------

peso_linha([], _, 0).
peso_linha([(X, _)|T], X, Peso) :-
  peso_linha(T, X, Peso1),
  Peso is Peso1+1, !.
peso_linha([_|T], X, Peso) :-
  peso_linha(T, X, Peso), !.

peso_linhas(Posicoes, 1, Res) :- peso_linha(Posicoes, 1, Res).

peso_linhas(Posicoes, Dim, Res) :-
  peso_linha(Posicoes, Dim, Col),
  NextDim is Dim-1,
  peso_linhas(Posicoes, NextDim, Res2),
  flatten([Res2|Col], Res), !.

verifica_linhas([_, Maximos, _], Ja_Preenchidas, Dim, Poss) :-
  append(Ja_Preenchidas, Poss, Preenchidas),
  sort(Preenchidas, Preenchidas_Ordenadas),
  peso_linhas(Preenchidas_Ordenadas, Dim, Pesos),
  compara_pesos(Pesos, Maximos).

% --------------------------------------------------------------------

linha(_, 0, Linha) :- Linha = [], !.

linha(X, Dim, Linha) :-
  NextDim is Dim-1,
  linha(X, NextDim, L),
  L2 = [(X, Dim)|L],
  sort(L2, Linha), !.

resolve_aux(_, _, _, 0, Ja_Preenchidas, Solucao) :-
  Solucao = Ja_Preenchidas.

resolve_aux([T, ML, MC], NrLinha, Dim, Count, Ja_Preenchidas, Solucao) :-
  linha(NrLinha, Dim, Linha),
  nth1(NrLinha, ML, Total),
  possibilidades_linha([T, ML, MC], Linha, Total, Ja_Preenchidas, Possibilidades),
  member(X, Possibilidades),
  verifica_linhas([T, ML, MC], Ja_Preenchidas, Dim, X),
  NextLinha is NrLinha+1,
  append(Ja_Preenchidas, X, AAA),
  sort(AAA, CCC),
  NextCount is Count-1,
  resolve_aux([T, ML, MC], NextLinha, Dim, NextCount, CCC, Solucao), !.

resolve([Termometros, Max_Linhas, Max_Cols], Solucao) :-
  length(Max_Linhas, Dim),
  resolve_aux([Termometros, Max_Linhas, Max_Cols], 1, Dim,  Dim, [], Solucao). 


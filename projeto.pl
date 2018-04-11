% Henrique Afonso Coelho Dias: 89455

%---------------------------------------------------------------------
% lista_que_contem(Listas, El, Lista) : dada a lista de listas Listas
% e o elemento El, Lista e a lista pertencente a Listas que contem El.
% --------------------------------------------------------------------
lista_que_contem([H|T], El, Lista) :-
  (member(El, H) -> Lista = H ; lista_que_contem(T, El, Lista)).

%---------------------------------------------------------------------
% propaga(Puz, Pos, Posicoes) : dado o puzzle Puz, o preenchimento da
% posicao Pos implica o preenchimento de todas as posicoes da lista
% ordenada Posicoes.
% --------------------------------------------------------------------

propaga([Listas, _, _], Pos, Posicoes) :-
  lista_que_contem(Listas, Pos, Lista),
  append(Lista2, [Pos|_], Lista),
  sort([Pos|Lista2], Posicoes), !.

%---------------------------------------------------------------------
% e_sublista(Lista1, Lista2) : e True se a lista Lista1 estiver contida
% na lista LIsta2.
% --------------------------------------------------------------------

e_sublista([], _).
e_sublista([X|Tail], Y):-
  member(X, Y),
  e_sublista(Tail, Y), !.

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

peso_colunas(Posicoes, 1, Res) :- peso_coluna(Posicoes, 1, Res).

% --------------------------------------------------------------------
% peso_colunas(Posicoes, Dim, Pesos) : dada uma lista de posicoes
% Posicoes e a dimensao Dim de um pizzle, Pesos e uma lista de Pesos
% onde cada numero corresponde a quantidade de colunas preenchidas
% numa dada coluna.
% --------------------------------------------------------------------

peso_colunas(Posicoes, Dim, Res) :-
  peso_coluna(Posicoes, Dim, Col),
  NextDim is Dim-1,
  peso_colunas(Posicoes, NextDim, Res2),
  flatten([Res2|Col], Res), !.

% --------------------------------------------------------------------
% menor_igual(X, Y) : X e menor ou igual a Y.
%
% compara_pesos(Pesos, Maximos) : a lista de pesos Pesos nao excede
% a lista de pesos maximos Maximos.
% --------------------------------------------------------------------

menor_igual(X,Y) :- (X =< Y -> true ; false).

compara_pesos([], []).

compara_pesos([H|T], [Z|V]) :-
  menor_igual(H, Z),
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

procura_aux([Listas, Max_Linhas, Max_Cols], (L, C), Ja_Preenchidas, Possibilidades_L) :-
  length(Max_Linhas, Dim),
  propaga([Listas, Max_Linhas, Max_Cols], (L, C), Posicoes),
  nao_altera_linhas_anteriores(Posicoes, L, Ja_Preenchidas),
  verifica_parcial([Listas, Max_Linhas, Max_Cols], Ja_Preenchidas, Dim, Posicoes),
  sort(Posicoes, Possibilidades_L), !.

procura_aux(_, _, _, Possibilidades_L) :- Possibilidades_L = [].

procura(_, [], _, _, P) :- P = [].

procura(Puz, [H|T], Total, Ja_Preenchidas, Possibilidades_L) :-
  procura_aux(Puz, H, Ja_Preenchidas, P1),
  procura(Puz, T, Total, Ja_Preenchidas, P2),
  Possibilidades_L = [P1|P2], !.

% --------------------------------------------------------------------

aaa(As, Bs, Line, Len) :-
  comb(As, Cs),
  sort(Cs, Ds),
  findall(X, member((Line, X), Ds), Fs),
  length(Fs, Len),
  Bs = Ds.

comb(As,Bs) :-
  same_length(As,Full),
  ASA = [_|_],
  prefix(ASA,Full),
  foldl(select,ASA,As,_),
  flatten(ASA, ASS),
  sort(ASS, Bs).

% --------------------------------------------------------------------

possibilidades_linha(_, _, 0, _, Possibilidades_L) :-
  Possibilidades_L = [[]].

possibilidades_linha(Puz, [(L, C)|K], Total, Ja_Preenchidas, Possibilidades_L) :-
  procura(Puz, [(L, C)|K], Total, Ja_Preenchidas, Posses),
  delete(Posses, [], Posses2),
  findall(X, aaa(Posses2, X, L, Total), P3),
  sort(P3, Possibilidades_L), !.

%Nome: Henrique Afonso Coelho Dias: 89455

:- [exemplos_puzzles].

lista_que_contem([H|T], X, Res) :-
  (member(X, H) -> Res = H ; lista_que_contem(T, X, Res)), !.

propaga([Listas, _, _], Pos, Posicoes) :-
  lista_que_contem(Listas, Pos, Lista),
  append(Lista2, [Pos|_], Lista),
  sort([Pos|Lista2], Posicoes), !.

e_sublista([], _).
e_sublista([X|Tail], Y):-
  member(X, Y),
  e_sublista(Tail, Y).

nao_altera_linhas_anteriores(Posicoes, L, Ja_Preenchidas) :-
  sort(Posicoes, Posicoes_ordenadas),
  append(Linhas_anteriores, [(L, _)|_], Posicoes_ordenadas),
  e_sublista(Linhas_anteriores, Ja_Preenchidas), !.

% peso_coluna(Posicoes, X, Peso)

peso_coluna([], _, 0).

peso_coluna([(_,X)|T], X, Peso) :-
  peso_coluna(T, X, Peso1),
  Peso is Peso1+1, !.

peso_coluna([_|T], X, Peso) :-
  peso_coluna(T, X, Peso), !.

peso_colunas(Posicoes, 1, Res) :- peso_coluna(Posicoes, 1, Res).

peso_colunas(Posicoes, Dim, Res) :-
  peso_coluna(Posicoes, Dim, Col),
  NextDim is Dim-1,
  peso_colunas(Posicoes, NextDim, Res2),
  flatten([Res2|Col], Res), !.

menorOuIgual(X,Y) :-
    ( X =< Y
       -> true
       ;  false
    ).

comparaPesos([H|T], [Z|V]) :-
  menorOuIgual(H, Z),
  comparaPesos(T, V).

verifica_parcial([_, _, Maximos], Ja_Preenchidas, Dim, Poss) :- 
  append(Ja_Preenchidas, Poss, Posicoes),
  peso_colunas(Posicoes, Dim, Pesos),
  comparaPesos(Pesos, Maximos), !.

% verifica_parcial(Puz, Ja_Preenchidas, Dim, Poss)
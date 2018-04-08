% Henrique Afonso Coelho Dias: 89455

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

% peso_colunas(Posicoes, Dimensao, Resposta)

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

comparaPesos([], []).

comparaPesos([H|T], [Z|V]) :-
  % write(H), write(' '), write(Z), nl,
  menorOuIgual(H, Z),
  % write('DONE'), nl,
  comparaPesos(T, V).

% verifica_parcial(Puz, Ja_Preenchidas, Dim, Poss)

verifica_parcial([_, _, Maximos], Ja_Preenchidas, Dim, Poss) :- 
  % nl, nl, write(Maximos), nl, write(Ja_Preenchidas), nl, write(Dim), nl, write(Poss), nl, nl, nl,
  append(Ja_Preenchidas, Poss, Posicoes),
  peso_colunas(Posicoes, Dim, Pesos),
  % write(Pesos), write(Maximos),
  comparaPesos(Pesos, Maximos), !.

procura_aux([Listas, Max_Linhas, Max_Cols], (L, C), Total, Ja_Preenchidas, Possibilidades_L) :-
  length(Max_Linhas, Dim),
  propaga([Listas, Max_Linhas, Max_Cols], (L, C), Posicoes),
  nao_altera_linhas_anteriores(Posicoes, L, Ja_Preenchidas),
  verifica_parcial([Listas, Max_Linhas, Max_Cols], Ja_Preenchidas, Dim, Posicoes),
  sort(Posicoes, Possibilidades_L), !.

procura(_, [], _, _, P) :- P = [].

procura(Puz, [H|T], Total, Ja_Preenchidas, Possibilidades_L) :-
  (procura_aux(Puz, H, Total, Ja_Preenchidas, P1) -> true ; P1 = [], true),
  %write(P1), nl,
  procura(Puz, T, Total, Ja_Preenchidas, P2),
  %write(P2), nl,
  Possibilidades_L = [P1|P2], !.

%poses_linhas([]).

%memberX((L, K), Ja_Preenchidas, L) :- member((L, K), Ja_Preenchidas).


possibilidades_linha(Puz, [(L, C)|K], Total, Ja_Preenchidas, Possibilidades_L) :-
  procura(Puz, [(L, C)|K], Total, Ja_Preenchidas, Posses),
  % write(Posses), nl,
  delete(Posses, [], Posses2),
  write(Posses2), nl,
  findall(X, aaa(Posses2, X, L, Total), P3),
  %write(L), nl,
  %findall(X, memberX(X, Ja_Preenchidas, L), Teste), write(Teste), nl,
  sort(P3, Possibilidades_L),
  % obter linha ja preenchida e adicionar a todas as possibilidades_L
  % sort(Ja_Preenchidas, Preenchidas_Sorted),
    !.

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
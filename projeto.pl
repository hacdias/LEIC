% Henrique Dias - 89455

:- [exemplos_puzzles].

antes_de([N|_], N, Res) :-
  Res = [N], !.

antes_de([H|T], N, Res) :-
  antes_de(T, N, Res2),
  Res = [H|Res2], !.

% antes_de(N, L, Result) :- append(Result, [N|_], List), !.

encontra([H|T], X, Res) :-
  (member(X, H) -> Res = H ; encontra(T, X, Res)), !.

propaga([Listas, _, _], Pos, Posicoes) :-
  encontra(Listas, Pos, Lista),
  append(Lista2, [Pos|_], Lista),
  sort([Pos|Lista2], Posicoes), !.
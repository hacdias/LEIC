% Henrique Dias - 89455

:- [exemplos_puzzles].

encontra([H|T], X, Res) :-
  (member(X, H) -> Res = H ; encontra(T, X, Res)), !.

propaga([Listas, _, _], Pos, Posicoes) :-
  encontra(Listas, Pos, Lista),
  append(Lista2, [Pos|_], Lista),
  sort([Pos|Lista2], Posicoes), !.

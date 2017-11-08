def filtra(teste, lst):
    res = list()
    for e in lst:
        if teste(e):
            res = res + [e]
    return res

def transforma(tr, lst):
    res = list()
    for e in lst:
        res = res + [tr(e)]
    return res

def acumula(fn, lst):
    res = lst[0]
    for e in lst[1:]:
        res = fn(res, e)
    return res

# conta_pares: list -> int
def conta_pares(lst):
    return acumula(lambda x, y: x + y,
                   transforma(lambda x: 1,
                              filtra(lambda x: x % 2 == 0, lst)))

print(conta_pares([1, 2, 3, 4, 5, 6, 7, 8]))
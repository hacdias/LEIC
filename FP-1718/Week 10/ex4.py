from math import sqrt

def cria_ponto(x, y):
    return (x, y)

def abcissa(pt):
    return pt[0]

def ordenada(pt):
    return pt[1]

def e_ponto(arg):
    return (
        isinstance(arg, tuple) and
        len(arg) == 2 and
        isinstance(arg[0], int) and
        isinstance(arg[1], int)
    )

def dist_pontos(p1, p2):
    return sqrt((abcissa(p1) - abcissa(p2)) ** 2 + (ordenada(p1) - ordenada(p2)) ** 2)

def quadrante(p):
    x = abcissa(p)
    y = ordenada(p)

    if x >= 0 and y >= 0:
        return 1
    elif x <= 0 and y >= 0:
        return 2
    elif x <= 0 and y <= 0:
        return 3
    else:
        return 4
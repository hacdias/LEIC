def agrupa_por_chaves(lst):
    res = {}

    for e in lst:
        if e[0] not in res:
            res[e[0]] = [e[1]]
        else:
            res[e[0]] = res[e[0]] + [e[1]]

    return res

def baralho():
    b = []

    for np in ('esp', 'copas', 'ouros', 'paus'):
        for vlr in ('A', '2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K'):
            b = b + [{ 'np': np, 'vlr': vlr }]

    return b

from random import random
from math import floor

def baralha(lst):
    cmp = len(lst)

    for i in range(cmp):
        j = floor(random() * cmp)
        lst[i], lst[j] = lst[j], lst[i]

    return lst

def distribui(lst):
    if len(lst) % 4 != 0:
        raise ValueError('argumentos invalidos')

    c = len(lst)

    return [
        lst[:c//4],
        lst[c//4:c//2],
        lst[c//2:c//4*3],
        lst[(c//4)*3:]
    ]

def metabolismo(lst):
    res = {}

    for name in lst:
        (s, i, h, p) = lst[name]

        if s == 'M':
            res[name] = 66 + 6.3 * p + 12.9 * h + 6.8*i
        else:
            res[name] = 655 + 4.3*p+4.7*h+4.7*i

    return res

def conta_palavras(cc):
    res = {}
    partido = cc.split(' ')

    for p in partido:
        if p not in res:
            res[p] = 1
        else:
            res[p] = res[p] + 1

    return res

def mais_antigo(bib):
    livro = bib[0]

    for l in bib:
        if l['ano'] < livro['ano']:
            livro = l

    return livro['titulo']


# TAD MATRIZ

def cria_matriz(n, m):
    if isinstance(n, int) and isinstance(m, int):
        return {}
    else:
        raise ValueError('cria_matriz: argumentos invalidos')

def set(matriz, linha, col, valor):
    if isinstance(linha, int) and isinstance(col, int) and \
        isinstance(valor, (int, float, double)) and \
        1 <= linha <= matriz['dim'][0] and \
        1 <= col <= matriz['dim'][1]:
        chave = (linha, col)
        matriz[chave] = valor
    else:
        raise ValueError('...')
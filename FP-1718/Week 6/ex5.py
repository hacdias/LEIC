def elemento_matriz(matriz, l, c):
    if l >= len(matriz):
        print('Indice invalido: linha', l)
    elif c >= len(matriz[0]):
        print('Indice invalido: coluna', c)
    else:
        return matriz[l][c]

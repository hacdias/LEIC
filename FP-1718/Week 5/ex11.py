def codifica(frase):
    impares = ''
    pares = ''

    for i in range(len(frase)):
        if (i+1) % 2 == 0:
            pares = pares + frase[i]
            continue

        impares = impares + frase[i]

    return impares+pares

def descodifica(frase):
    comprimento = len(frase)
    metade = comprimento // 2

    if comprimento % 2 != 0:
        metade = metade + 1

    impares = frase[:metade]
    pares = frase[metade:]

    frase = ''

    for i in range(metade):
        frase = frase + impares[i]
        if not (i == metade - 1 and comprimento % 2 != 0):
            frase += pares[i]

    return frase

def filtra_pares(numeros):
    pares = ()

    for n in numeros:
        if n % 2 != 0:
            continue

        pares = pares + (n, )

    return pares

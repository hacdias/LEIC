def explode(n):
    if not isinstance(n, int):
        raise ValueError('explode: argumento nao inteiro')
  
    tuplo = ()

    while n > 0:
        tuplo = (n%10,) + tuplo
        n = n // 10

    return tuplo

def implode(digitos):
    n = 0

    for el in digitos:
        if isinstance(el, int) and 0 <= el <= 9:
            n = n * 10 + el
        else:
            raise ValueError('implode: elemento nao inteiro')

    return n

def filtra_pares(numeros):
    pares = ()

    for n in numeros:
        if n % 2 != 0:
            continue

        pares = pares + (n, )

    return pares

def algarismos_pares(n):
    return implode(filtra_pares(explode(n)))

def misterio(x, n):
    def misterio_aux(soma, i):
        if i == 0:
            return soma
        else:
            return misterio_aux(soma + x * i, i - 1)

    return misterio_aux(0, n)

def quadrado_rec_adiada(n):
    def aux(n):
        if n == 0:
            return 0
        elif n % 2 == 0:
            return aux(n - 1)
        else:
            return n + aux(n - 1)

    return aux(n + n)

def quadrado_rec_cauda(n):
    def aux(i, soma):
        if i == 0:
            return soma
        elif i % 2 == 0:
            return aux(i - 1, soma)
        else:
            return aux(i - 1, soma + i)

    return aux(n + n, 0)

def quadrado_iterativo(n):
    n = n + n
    quadrado = 0

    while n != 0:
        if n % 2 != 0:
            quadrado = quadrado + n
        n = n - 1

    return quadrado

def numero_digitos_rec_op_adiadas(n):
    if n == 0:
        return 0
    else:
        return 1 + numero_digitos_rec_op_adiadas(n // 10)

def numero_digitos_rec_cauda(n):
    def aux(i, digitos):
        if i == 0:
            return digitos
        else:
            return aux(i // 10, digitos + 1)

    return aux(n, 0)

def numero_digitos_iterativo(n):
    digitos = 0

    while n != 0:
        digitos = digitos + 1
        n = n // 10

    return digitos

def e_capicua(n):
    def aux(num, i):
        if i == 0:
            return True
        elif num[0] != num[-1]:
            return False
        else:
            return aux(num[1:-1], i - 1)

    return aux(str(n), numero_digitos_iterativo(n) / 2)

def espelho(n):
    def aux(inicial, final):
        if inicial == 0:
            return final
        else:
            return aux(inicial // 10, final * 10 + (inicial % 10))
    
    return aux(n, 0)

def maior_inteiro(limite):
    def aux(n, soma):
        if soma - n < 0:
            return n - 1
        else:
            return aux(n + 1, soma - n)
    
    return aux(1, limite)

print(maior_inteiro(7))

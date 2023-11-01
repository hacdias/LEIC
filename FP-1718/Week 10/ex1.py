# TIPO RACIONAL
# Operações básicas:
#   1. Numerador
#   2. Denominador
#
# Representação:
#   [<numerador>, <denominador>]

def cria_rac(num, den):
    return [num, den]

def denominador(racional):
    return racional[1]

def numerador(racional):
    return racional[0]

def soma_rac(r1, r2):
    return [
        numerador(r1)*denominador(r2)+numerador(r2)*denominador(r1),
        denominador(r1)*denominador(r2)
    ]

def produto_rac(r1, r2):
    return [
        numerador(r1)*numerador(r2),
        denominador(r1)*denominador(r2)
    ]

def escreve_rac(r):
    print(str(numerador(r)) + '/' + str(denominador(r)))

# 89455 - Henrique Dias
# A representação é (x, y)

def vetor(x, y):
    '''
    vetor: real x real --> vetor
    '''
    if not (isinstance(x, float) and isinstance(y, float)):
        raise ValueError('vetor: argumentos invalidos')

    return (x, y)

def abcissa(v):
    '''
    abcissa: vetor --> real
    '''
    return v[0]

def ordenada(v):
    '''
    ordenada: vetor --> real
    '''
    return v[1]

def eh_vetor(arg):
    '''
    eh_vetor: universal --> logico
    '''
    return (
        isinstance(arg, tuple) and
        len(arg) == 2 and
        isinstance(arg[0], float) and
        isinstance(arg[1], float)
    )

def eh_vetor_nulo(v):
    '''
    eh_vetor_nulo: vetor --> logico
    '''
    return eh_vetor(v) and abcissa(v) == 0 and ordenada(v) == 0

def vetores_iguais(v1, v2):
    '''
    vetores_iguais: vetor X vetor --> lógico
    '''
    return abcissa(v1) == abcissa(v2) and ordenada(v1) == ordenada(v2)

def produto_escalar(v1, v2):
    '''
    produto_escalar: vetor X vetor --> real
    '''
    return abcissa(v1) * abcissa(v2) + ordenada(v1) * ordenada(v2)


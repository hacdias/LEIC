def cria_complexo(r, i):
    '''cria_complexo: real X real --> complexo'''
    if isinstance(r, (int, float)) and isinstance(i, (int, float)):
        return {'real': r, 'imaginario': i}
    else:
        raise ValueError('cria_complexo:argumento invalidos')

def parte_real(c):
    '''parte_real: complexo --> real'''
    return c['real']

def parte_imaginaria(c):
    '''parte_imaginaria: complexo --> real'''
    return c['imaginario']

def e_complexo(c):
    '''e_complexo: universal --> logico'''
    return isinstance(c, dict) and \
        len(c) == 2 and 'real' in c and 'imaginario' in c and \
        isinstance(parte_real(c), (int, float)) and \
        isinstance(parte_imaginaria(c), (int, float))

def imprime_complexo(c):
    '''imprime_complexo: complexo -->'''
    r = parte_real(c)
    i = parte_imaginaria(c)

    if i < 0:
        print(r, '-', str(abs(i)) + 'i')
    else:
        print(r, '+', str(i) + 'i')

def complexos_iguais(c1, c2):
    '''complexos_iguais: complexo x complexo --> logico'''
    return parte_real(c1) == parte_real(c2) and \
        parte_imaginaria(c1) and parte_imaginaria(c2)

def soma_complexos(c1, c2):
    '''soma_complexos: complexo x complexo --> complexo'''
    return cria_complexo(
        parte_real(c1) + parte_real(c2),
        parte_imaginaria(c1) + parte_imaginaria(c2)
    )

c = cria_complexo(2, 3)
imprime_complexo(c)
print(e_complexo(c))

c2 = cria_complexo(4, -20.2)
imprime_complexo(c2)
print(e_complexo(c2))

print(complexos_iguais(c, c2))

imprime_complexo(soma_complexos(c, c2))
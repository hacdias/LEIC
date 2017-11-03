from math import floor

def cinco(x):
    return x == 5

def horas_dias(h):
    return h / 24

def area_circulo(r):
    return 3.14 * r ** 2

def area_coroa(r1, r2):
    if r1 > r2:
        raise ValueError('r1 maior que r2')

    return area_circulo(r2) - area_circulo(r1)

QUANTIAS = (50, 20, 10, 5, 2, 1, 0.5, 0.2, 0.1, 0.05, 0.02, 0.01)

def moedas_notas(x):
    for quantia in QUANTIAS:
        print(int(x // quantia), 'x', quantia, '€')
        x %= quantia

    # Pode sobrar um pouco por causa dos floating points. Como resolver?

def bissexto(ano):
    return (ano % 4 == 0 and ano % 100 != 0) or ano % 400 == 0

MESES = (
    'jan',
    'fev',
    'mar',
    'abr',
    'mai',
    'jun',
    'jul',
    'ago',
    'set',
    'out',
    'nov',
    'dez'
)

def mes_valido(mes):
    for m in MESES:
        if mes == m:
            return True

    return False


def dias_mes(mes, ano):
    if not mes_valido(mes):
        raise ValueError('mes invalido')
    
    if mes == 'fev':
        return 29 if bissexto(ano) else 28
    elif mes == 'nov' or mes == 'abr' or mes == 'jun' or mes == 'set':
        return 30
    else:
        return 31

def valor(q, j, n):
    if not 0 < j < 1:
        raise ValueError('juros invalidos')

    return q * (1 + j) ** n

def duplicar(q, j):
    anos = 0

    while valor(q, j, anos) < 2 * q:
        anos += 1

    return anos

def serie_geom(r, n):
    if n < 0:
        raise ValueError('argumento incorreto')

    soma = 0

    for i in range(0, n+1):
        soma += r ** i

    return soma

def calcular_dia(q, m, a):
    if m == 1 or m == 2:
        a -= 1
        m = 12 + m

    k = a % 100
    j = a // 100

    return (q + floor((13 * (m + 1)) / 5) + k + floor(k / 4) + floor(j / 4) - 2 * j) % 7

def dia_da_semana(q, m, a):
    h = calcular_dia(q, m, a)

    if h == 0:
        return 'sabado'
    elif h == 1:
        return 'domingo'
    elif h == 2:
        return 'segunda'
    elif h == 3:
        return 'terca'
    elif h == 4:
        return 'quarta'
    elif h == 5:
        return 'quinta'
    else:
        return 'sexta'

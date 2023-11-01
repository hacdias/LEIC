def valida_data(d, m, a):
    return (
        1 <= d <= 31 and
        1 <= m <= 12
    )

def cria_data(d, m, a):
    if not valida_data(d, m, a):
        raise ValueError('cria_Data: argumentos invalidos')

    return (d, m, a)

def dia(data):
    return data[0]

def mes(data):
    return data[1]

def ano(data):
    return data[2]

def e_data(data):
    return (
        isinstance(data, tuple) and 
        len(data) == 3 and
        valida_data(data[0], data[1], data[2])
    )

def mesma_data(d1, d2):
    return (
        ano(d1) == ano(d2) and
        mes(d1) == mes(d2) and 
        dia(d1) == dia(d2)
    )

def numero_com_zeros(n, zeros):
    num = str(n)

    while len(num) != zeros:
        num = '0' + num

    return num

def escreva_data(data):
    msg = ''
    d = dia(data)
    m = mes(data)
    a = ano(data)

    if d < 10:
        msg = '0' + str(d)
    else:
        msg = str(d)

    if m < 10:
        msg = msg + '/0' + str(m) + '/'
    else:
        msg = msg + '/' + str(m) + '/'

    msg = msg + numero_com_zeros(abs(a), 4)

    if a < 0:
        msg = msg + ' AC'

    print(msg)

def data_anterior(data1, data2):
    d1 = dia(data1)
    d2 = dia(data2)
    m1 = mes(data1)
    m2 = mes(data2)
    a1 = ano(data1)
    a2 = ano(data2)

    if a1 != a2:
        return a1 < a2

    if m1 != m2:
        return m1 < m2

    return d1 < d2

def idade(nasc, hoje):
    diff = ano(hoje) - ano(nasc)

    if diff == 0:
        return 0

    if mes(hoje) != mes(nasc):
        if mes(hoje) < mes(nasc):
            diff = diff - 1
        return diff

    if dia(hoje) < dia(nasc):
        diff = diff - 1

    return diff

print(idade(cria_data(2, 1, 2003), cria_data(2, 3, 2006)))
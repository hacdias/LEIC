def cria_relogio(h, m, s):
    if not (0 <= h <= 23 and
        0 <= m <= 59 and 
        0 <= s <= 59):
        raise ValueError('argumentos invalidos')

    return {
        'horas': h,
        'min': m,
        'seg': s
    }

def horas(relogio):
    return relogio['horas']

def minutos(relogio):
    return relogio['min']

def segs(relogio):
    return relogio['seg']

def escreve_relogio(relogio):
    msg = ''

    if horas(relogio) < 10:
        msg = '0' + str(relogio['horas'])
    else:
        msg = str(relogio['horas'])

    if minutos(relogio) < 10:
        msg = msg + ':0' + str(relogio['min']) + ':'
    else:
        msg = msg + ':' + str(relogio['min']) + ':'

    if segs(relogio) < 10:
        msg = msg + '0' + str(relogio['seg'])
    else:
        msg = msg + str(relogio['seg'])

    print(msg)

def diferenca_segundos(rel1, rel2):
    inst_inicial = horas(rel1) * 3600 + minutos(rel2) * 60 + segs(rel1)
    inst_final = horas(rel2) * 3600 + minutos(rel2) * 60 + segs(rel2)

    if inst_inicial > inst_final:
        raise ValueError('primeiro relogio nao pode ter um instante maior q o sehundo')

    return inst_final - inst_inicial

escreve_relogio(cria_relogio(9, 2, 34))
print(diferenca_segundos(cria_relogio(10, 2, 34), cria_relogio(11, 2, 34)))

segs = int(input('Escreva o número de segundos: '))

dias = segs // 86400
segs %= 86400

horas = segs // 3600
segs %= 3600

mins = segs // 60
segs %= 60

print('dias:', dias, 'horas:', horas, 'mins:', mins, 'segs:', segs)
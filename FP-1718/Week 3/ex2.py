d = int(input('Que distancia percorreu (em Kms)? '))
t = int(input('Quanto tempo demorou (em minutos)? '))

kmh = round(d/(t/60), 2)
ms = round((d*1000)/(t*60), 2)

print('A sua velocidade média foi de:\n')
print('a)', kmh, 'km/h')
print('b)', ms, 'm/s')

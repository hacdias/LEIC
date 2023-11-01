num = ''

while True:
    digito = input('Escreva um dígito (-1 para terminar)?\n')
 
    if digito == '-1':
        break

    num += digito

print('O numero e', int(num))
    
n = int(input('Escreva um numero inteiro: '))
zeros = 0

while n != 0:
    if n % 100 == 0:
        zeros += 1

    n //= 10

print('O numero tem', zeros, 'zeros seguidos')
n = int(input('Insira um numero para ser invertido: '))
inv = 0

while n != 0:
    inv *= 10
    inv += n % 10
    n //= 10

print('O número invertido é', inv)

n = int(input('Insira um valor para somar os seus digitos: '))
s = 0

while n != 0:
    s += n % 10
    n //= 10

print('A soma dos digitos e', s)
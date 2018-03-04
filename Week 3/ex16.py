n = int(input('Insira um número inteiro: '))
c = n

while c != 0:
    n = n * 10 + c % 10
    c //= 10

print('A capicua e', n)
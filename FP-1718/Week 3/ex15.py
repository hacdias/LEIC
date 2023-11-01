print('Insira uma sequência de dígitos, terminando com -1, para formar um número:')
n = 0
d = int(input())

while d != -1:
    n = n * 10 + d
    d = int(input())

print('O numero formado é', n)
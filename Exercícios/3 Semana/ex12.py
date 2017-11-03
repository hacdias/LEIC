x = int(input('Qual o valor de x? '))
n = int(input('Qual o valor de n? '))

soma, prev, current = 1, 1, 1

while current != n+1:
    prev *= x/current
    soma += prev
    current += 1

print('O valor da soma e', soma)

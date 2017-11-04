limite = eval(input('Introduza o limite: '))
n = 0
sum = 0

while sum + n + 1 <= limite:
    n = n + 1
    sum = sum + n

print('O N e', n)

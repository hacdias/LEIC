num = int(input('Escreva um numero inteiro: '))
final = 0
count = 1

while num != 0:
    d = num % 10

    if d % 2 != 0:
        final = (count * d) + final
        count *= 10

    num //= 10

print(final)
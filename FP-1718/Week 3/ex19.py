n = float(input('Insira quanto dinheiro tem: '))

quantias = [50, 20, 10, 5, 2, 1, 0.5, 0.2, 0.1, 0.05, 0.02, 0.01]

print('Pode utilizar:\n')

for quantia in quantias:
    print(int(n // quantia), 'x', quantia, '€')
    n %= quantia

# Pode sobrar um pouco por causa dos floating points...
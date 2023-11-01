alunos = int(input('Insira o número de alunos: '))
positivas = 0
negativas = 0

print('Agora deve inserir cada nota (0 a 20) numa linha diferente:')

while alunos != 0:
    n = float(input())

    if n >= 10:
        positivas += 1
    else:
        negativas += 1

    alunos -= 1

percentagem = round(positivas / (negativas+positivas) * 100, 1)

print(positivas, 'alunos tiveram nota positiva')
print(negativas, 'alunos tiveram nota negativa')
print(percentagem, 'foi a percentagem de positivas')

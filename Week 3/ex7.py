horas = eval(input('Insira o numero de horas que trabalhou: '))
salario = eval(input('Insira o salario/hora que recebe: '))

if horas < 40:
    print('Ira receber', horas*salario, 'euros.')
else:
    horas -= 39
    print('Ira receber', 39*salario+horas*salario*2, 'euros')

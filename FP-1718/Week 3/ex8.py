MENSAGEM = 'Escreva um numero de segundos (negativo para terminar)? '
segs = int(input(MENSAGEM))

while segs >= 0:
    print('O numero de dias correspondente e', segs / 86400)
    segs = int(input(MENSAGEM))

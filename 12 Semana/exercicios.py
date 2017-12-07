def conta_linhas(nome):
    lines = open(nome, 'r').readlines()
    count = 0

    for line in lines:
        if line == '\n':
            count = count + 1

    return count

def conta_vogais(nome):
    file = open(nome, 'r')
    count = {
        'a': 0,
        'e': 0,
        'i': 0,
        'o': 0,
        'u': 0
    }

    line = file.readline()
    while line != '':
        for c in line:
            if c in ('a', 'e', 'i', 'o', 'u'):
                count[c] = count[c] + 1
    
    file.close()
    return count

def inverte(n1, n2):
    fich1 = open(n1)
    fich2 = open(n2, 'w')

    linhas = fich1.lines()
    for i in range(len(linhas) - 1, -1, -1):
        fich2.write(linhas[i])
    
    fich1.close()
    fich2.close()

def concatena(in1, in2, out):
    fich1 = open(in1)
    fich2 = open(in2)
    fich3 = open(out, 'w')

    linhas = fich1.lines() + fich2.lines()
    fich3.write(str.join('', linhas))

def procura(cc, nome):
    linhas = open(nome).lines()
    for l in linhas:
        if l.find(cc) != -1:
            print(l)

def corta(inf, out, n):
    fich1 = open(inf)
    fich2 = open(out, 'w')

    linha = fich1.readline()
    while n != 0 or linha != '':
        fich2.write(linha[0])
        linha = linha[1:]

        if linha == '':
            linha = fich1.readline()

        n = n - 1

    fich1.close()
    fich2.close()

def ordena(nome):
    fich1 = open(nome)
    linhas = fich1.lines()
    fich1.close()

    linhas.sort()
    fich1 = open(nome, 'w')

    for l in linhas:
        fich1.write(l)

    fich1.close()

def divide(nome, n):
    fich = open(nome, 'r')
    fich0 = open(nome + '0', 'w')
    fich1 = open(nome + '0', 'w')


def recorta(nome, n):
    file = open(nome)
    counter, linha = 0, file.read(n)

    while linha != '':
        out = open(nome + str(counter), 'w')
        out.write(linha)
        out.close()
        linha = file.read(n)
        counter = counter + 1

    file.close()

def conta_dupicacoes(nome):
    file = open(nome)
    tamanho, repeticoes, line = 0, {}, file.readline()

    while line != '':
        tamanho += 1
        for i in range(1, len(file)):
            if line[i] == line[i-1]:
                if line[i] not in repeticoes:
                    repeticoes[line[i]] = 1
                else:
                    repeticoes[line[i]] += 1
        line = file.readline()

    file.close()
    return (tamanho, repeticoes)

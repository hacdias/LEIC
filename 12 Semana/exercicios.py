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
    
    return count
def ficheiro_ordenado(nome):
    file = open(nome)
    linhas = file.readlines()
    file.close()

    for i in range(1, len(linhas)):
        if linhas[i] <= linhas[i-1]:
            return False

    return True

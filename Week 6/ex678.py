def imprimir_matriz(m):
    for linha in m:
        for el in linha:
            print(el, "\t", end='')
    
        print("\n")

def soma_matriz(m1, m2):
    for i in range(len(m1)):
        for j in range(len(m1[i])):
            m1[i][j] = m1[i][j] + m2[i][j]

    return m1

def multiplica_matriz(m1, m2):
    res = []
    dim = len(m1)

    for i in range(dim):
        linha = []

        for j in range(dim):
            v = 0

            for k in range(dim):
                v = v + m1[i][k] * m2[k][j]

            linha.append(v)

        res.append(linha)

    return res

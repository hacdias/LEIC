def soma_divisores(n):
    def aux(i, soma):
        if i > n:
            return soma
        elif n % i == 0:
            return aux(i+1, soma + i)
        else:
            return aux(i+1, soma)

    return aux(1, 0)

print(soma_divisores(0))
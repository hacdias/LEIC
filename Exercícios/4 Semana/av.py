def soma_divisores(n):
    soma = n

    for i in range(1, n//2+1):
        if n % i == 0:
            soma = soma + i

    return soma

def apenas_digitos_impares(n):
    def aux(n, i):
        if n == 0:
            return 0

        if (n % 10) % 2 == 0:
            return aux(n // 10, i)

        return (n % 10) * 10**i + aux(n // 10, i + 1)

    return aux(n, 0)

print(apenas_digitos_impares(123456789))
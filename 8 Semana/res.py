def piatorio(l_inf, l_sup, func):
    soma = 1
    while l_inf <= l_sup:
        soma = soma * func(l_inf)
        l_inf = l_inf + 1
    return soma

def fatorial(n):
    return piatorio(1, n, lambda x : x)

def filtra(tst, lst):
    if lst == []:
        return []

    if tst(lst[0]):
        return [lst[0]] + filtra(tst, lst[1:])

    return filtra(tst, lst[1:])

def transforma(fn, lst):
    if lst == []:
        return []

    return [fn(lst[0])] + transforma(fn, lst[1:])

def acumula(fn, lst):
    if lst == []:
        return 0

    return fn(lst[0]) + acumula(fn, lst[1:])

def soma_quadrados_impares(lst):
    return acumula(lambda x : x ** 2, filtra(lambda x : x % 2 != 0, lst))

def todos_lista(lst, cond):
    return (lst == []) or (cond(lst[0]) and todos_lista(lst[1:], cond))

def concentra(op, num):
    if num < 10:
        return num
    else:
        return op(num % 10, concentra(op, num // 10))

def produto(n):
    return concentra(lambda x, y: x * y, n)

def muda(fn, num):
    if num < 10:
        return fn(num)
    else:
        nn = fn(num % 10)
        return nn + 10 ** num_digitos(nn) * muda(fn, num // 10)

def num_digitos(n):
    if n < 10:
        return 1
    else:
        return 1 + num_digitos(n // 10)

def soma_dois(n):
    return muda(lambda x: x + 2, n)

def junta_listas(lst):
    return acumula(lambda x: x, lst)

def nenhum_p(n, fn):
    for i in range(1, n+1):
         if fn(i):
             return False
    return True

def soma_quadrados(n):
    return acumula(lambda x : x ** 2, list(range(1, n + 1)))

def lista_digitos(n):
    return transforma(lambda x: int(x), list(str(n)))
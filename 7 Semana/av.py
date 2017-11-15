def max_lista(lst):
    def aux(maximo, lst):
        if lst == []:
            return maximo
        elif lst[0] > maximo:
            return aux(lst[0], lst[1:])
        else:
            return aux(maximo, lst[1:])

    return aux(lst[0], lst[1:])

def max_lista_2(lst):
    if len(lst) == 1:
        return lst[0]

    return max(max_lista(lst[1:]), lst[0])

def max_lista_3(lst):
    def aux(maximo, lst):
        if lst == []:
            return maximo

        return aux((lst[0] + maximo + abs(lst[0] - maximo) >> 1), lst[1:])

    return aux(lst[0], lst[1:])

print(max_lista_3([-1,-2,-3,-4,-5]))

def num_occ_lista(lst, n):
    if lst == []:
        return 0
    elif lst[0] == n:
        return 1 + num_occ_lista(lst[1:], n)
    elif isinstance(lst[0], list):
        return num_occ_lista(lst[0], n) + num_occ_lista(lst[1:], n)
    else:
        return num_occ_lista(lst[1:], n)

print(num_occ_lista([1,[[1]]], 1))
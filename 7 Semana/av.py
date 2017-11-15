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

print(max_lista([1,2,5,6,7,8,9,157458548,3,4]))
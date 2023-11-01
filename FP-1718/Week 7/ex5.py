def troca_occ_lista(lst, a, b):
    if lst == []:
        return []

    if lst[0] == a:
        return [b] + troca_occ_lista(lst[1:], a, b)

    return [lst[0]] + troca_occ_lista(lst[1:], a, b)

print(troca_occ_lista([], True, 2))
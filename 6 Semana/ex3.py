def junta_ordenadas(lst1, lst2):
    len_1 = len(lst1)
    len_2 = len(lst2)
    len_total = len_1 + len_2
    prod = []

    i, j = 0, 0

    while len(prod) != len_total:
        if j >= len_2 or lst1[i] < lst2[j]:
            prod.append(lst1[i])
            i = i + 1
        else:
            prod.append(lst2[j])
            j = j + 1

    return prod

print(junta_ordenadas([2, 5, 90, 100, 120], [3, 5, 6, 12]))
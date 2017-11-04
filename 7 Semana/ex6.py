def inverte(lst):
    if lst == []:
        return []

    return lst[-1:] + inverte(lst[:-1])

print(inverte([3,4,7,9]))
def subtrai(lst, out):
    if lst == [] or out == []:
        return lst

    # so funciona se as listas estiverem ordenadas
    if lst[0] == out[0]:
        return subtrai(lst[1:], out[1:])

    return lst[:1] + subtrai(lst[1:], out)

print(subtrai([2,3,4,5], [3,5]))
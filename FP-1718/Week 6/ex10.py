def inverte_lista(lst):
    l = len(lst)

    for i in range(l // 2):
        lst[i], lst[l - i - 1] = lst[l - i - 1], lst[i]

    return lst

def duplica_lista(lst):
    for i in range(0, len(lst) * 2, 2):
        lst = lst[:i + 1] + [lst[i]] + lst[1+i:]

    return lst

def pertence(lst, e):
    i = 0
    com = len(lst)

    while i < com:
        if lst[i] == e:
            return True
        i = i + 1

    return False

def remove_repetidos(lst):
    i = 0

    while i < len(lst):
        j = i + 1
        while j < len(lst):
            if lst[i] == lst[j]:
                del(lst[j])
            else:          
                j = j + 1
        i = i + 1

    return lst

print(remove_repetidos([1,1,2,2,5,4,8,4,5,88,8,8,8,8,8,8,8,8,3,4,5,6]))
def pertence(lst, a):
    if lst == []:
        return False

    if lst[0] == a:
        return True

    return pertence(lst[1:], a)

print(pertence([3,4,5], 4))
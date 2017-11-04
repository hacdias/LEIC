def maior(lst):
    def aux(lst, n):
        if lst == []:
            return 0

        if lst[0] > n:
            return max(lst[0], aux(lst[1:], n))

        return max(n, aux(lst[1:], n))

    return aux(lst, 0)

print(maior([5, 3, 8, 1, 9, 2]))
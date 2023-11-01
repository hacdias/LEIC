def parte(lst, n):
    def parte_aux(lst, mai, men, n):
        if lst == []:
            return [men, mai]

        if lst[0] >= n:
            return parte_aux(lst[1:], mai + [lst[0]], men, n)

        return parte_aux(lst[1:], mai, men + [lst[0]], n)

    return parte_aux(lst, [], [], n)

print(parte([3, 5, 1, 4, 5, 8, 9], 4))
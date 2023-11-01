def remove_multiplos(lst, n):
        i, j = 0, 0

        while i < len(lst):
            if lst[i] % n == 0:
                del(lst[i])
            else:
                i = i + 1

        return lst 
    
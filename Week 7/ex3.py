def sublistas(lst):
    if lst == []:
        return 0

    if isinstance(lst[0], list):
        return 1 + sublistas(lst[0]) + sublistas(lst[1:])

    return sublistas(lst[1:])

print(sublistas(['a', [2, 3, [[[1]], 6, 7], 'b']]))
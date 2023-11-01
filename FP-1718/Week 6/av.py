def substitui_lista(lst, velho, novo):
    for i in range(len(lst)):
        if lst[i] == velho:
            lst[i] = novo

    return lst

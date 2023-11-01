def num_para_seq_cod(num):
    tpl = ()

    while num != 0:
        d = num % 10
        num = num // 10

        if d % 2 == 0:
            d = d + 2
            if d > 8:
                d = 0
        else:
            d = d - 2
            if d < 1:
                d = 9

        tpl = (d, ) + tpl

    return tpl

print(num_para_seq_cod(1234567890))
        
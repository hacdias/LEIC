def cc_para_int(s):
    n = 0

    for c in s:
        n = n* 1000 + ord(c)

    return n

print(cc_para_int('bom dia'))
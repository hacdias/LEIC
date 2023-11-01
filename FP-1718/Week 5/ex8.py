def e_num(s):
    return s in ('1', '2', '3', '4')

def e_letra(s):
    return s in ('A', 'B', 'C', 'D')

def e_numeros(s):
    if len(s) == 1 and not e_num(s):
        return False

    return e_num(s) or \
           (e_num(s[0]) and e_numeros(s[1:]))

def e_letras(s):
    if len(s) == 1 and not e_letra(s):
        return False

    return e_letra(s) or \
           (e_letra(s[0]) and e_letras(s[1:]))

def reconhece(s):
    for i in range(1, len(s)):
        if e_letras(s[:i]) and e_numeros(s[i:]):
            return True

    return False
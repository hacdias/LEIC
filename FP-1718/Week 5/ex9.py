def ter(s):
    return s == 'c' or (s[0] == 'c' and ter(s[1:]))

def seg(s):
    if not (s[0] == 'b' and s[-1] == 'b'):
        return False

    return seg(s[1:len(s)-1]) or ter(s[1:len(s)-1])

def reconhece(s):
    if not (s[0] == 'a' and s[-1] == 'a'):
        return False

    return seg(s[1:len(s)-1]) or reconhece(s[1:len(s)-1])



print(reconhece('abcccccb'))
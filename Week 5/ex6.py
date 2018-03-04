def amigas(str1, str2):
    l = len(str1)

    if l != len(str2):
        return False
   
    dif = 0

    for i in range(l):
        if str1[i] != str2[i]:
            dif = dif + 1

    return dif == 0 or dif/l <= 0.1

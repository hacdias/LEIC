def combos(c1, c2):
    caracteres = ()

    for i in range(ord(c1), ord(c2)+1):
        caracteres = caracteres + (chr(i), )

    for i in caracteres:
        for j in caracteres:
            for k in caracteres:
                print(i+j+k)

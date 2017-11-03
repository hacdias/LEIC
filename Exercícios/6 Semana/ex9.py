def seq_racman(n):
    seq = [0]
    i = 1

    while i < n:
        prev = seq[i-1]

        if prev > i and prev - i not in seq:
            seq.append(prev - i)
        else:
            seq.append(prev + i)

        i = i + 1
    return seq

print(seq_racman(15))
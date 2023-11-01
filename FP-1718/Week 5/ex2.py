def explode(n):
    if not isinstance(n, int):
        raise ValueError('explode: argumento nao inteiro')
  
    tuplo = ()

    while n > 0:
        tuplo = (n%10,) + tuplo
        n = n // 10

    return tuplo

# 89455 - Henrique Dias

def e_silaba(caracteres):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico associado
    ao ser silaba ou nao, de acordo com a gramatica.

    e_silaba: string -> logico
    """

    if not isinstance(caracteres, str):
        raise ValueError('e_silaba:argumento invalido')

    return e_vogal(caracteres) or \
           e_silaba_2(caracteres) or \
           e_silaba_3(caracteres) or \
           e_silaba_4(caracteres) or \
           e_silaba_5(caracteres)


def e_monossilabo(caracteres):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico associado
    ao ser monossilabo ou nao, de acordo com a gramatica.

    e_monossilabo: string -> logico
    """

    if not isinstance(caracteres, str):
        raise ValueError('e_monossilabo:argumento invalido')

    return e_vogal_palavra(caracteres) or \
           e_monossilabo_2(caracteres) or \
           e_monossilabo_3(caracteres)


def e_palavra(caracteres):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser palavra ou nao, de acordo com a gramatica.

    e_palavra: string -> logico
    """
    def e_palavra_aux(s, silaba_fn, i):
        """
        FAZER
        """
        if i == 0:
            return False

        return silaba_fn(s) or \
            silaba_fn(s[-i:]) and e_palavra_aux(s[:-i], e_silaba, min(5, len(s) - i)) or \
            e_palavra_aux(s, silaba_fn, i - 1)

    if not isinstance(caracteres, str):
        raise ValueError('e_palavra:argumento invalido')

    return e_monossilabo(caracteres) or \
           e_palavra_aux(caracteres, e_silaba_final, min(5, len(caracteres)))

def e_silaba_2(s):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser uma silaba de 2 caracteres ou nao, de acordo
    com a gramatica.

    e_silaba_2: cadeia de caracteres -> logico
    """

    if len(s) != 2:
        return False

    return e_par_vogais(s) or \
           (e_consoante(s[0]) and e_vogal(s[1])) or \
           (e_vogal(s[0]) and e_consoante_final(s[1]))


def e_silaba_3(s):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser uma silaba de 3 caracteres ou nao, de acordo
    com a gramatica.

    e_silaba_3: cadeia de caracteres -> logico
    """

    if len(s) != 3:
        return False

    return s in ('QUA', 'QUE', 'QUI', 'GUE', 'GUI') or \
           (e_vogal(s[0]) and s[1:] == 'NS') or \
           (e_consoante(s[0]) and e_par_vogais(s[1:])) or \
           (e_consoante(s[0]) and e_vogal(s[1]) and e_consoante_final(s[2])) or \
           (e_par_vogais(s[:2]) and e_consoante_final(s[2])) or \
           (e_par_consoantes(s[:2]) and e_vogal(s[2]))


def e_silaba_4(s):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser uma silaba de 4 caracteres ou nao, de acordo
    com a gramatica.

    e_silaba_4: cadeia de caracteres -> logico
    """

    if len(s) != 4:
        return False

    return (e_par_vogais(s[:2]) and s[2:] == 'NS') or \
           (e_consoante(s[0]) and e_vogal(s[1]) and s[2:] == 'NS') or \
           (e_consoante(s[0]) and e_vogal(s[1]) and s[2:] == 'IS') or \
           (e_par_consoantes(s[:2]) and e_par_vogais(s[2:])) or \
           (e_consoante(s[0]) and e_par_vogais(s[1:3]) and e_consoante_final(s[3]))


def e_silaba_5(s):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser uma silaba de 5 caracteres ou nao, de acordo
    com a gramatica.

    e_silaba_5: cadeia de caracteres -> logico
    """

    if len(s) != 5:
        return False

    return e_par_consoantes(s[:2]) and e_vogal(s[2]) and s[3:] == 'NS'


def e_silaba_final(s):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser uma silaba final ou nao, de acordo com a gramatica.

    e_silaba_final: cadeia de caracteres -> logico
    """

    return e_monossilabo_2(s) or e_monossilabo_3(s) or e_silaba_4(s) or e_silaba_5(s)


def e_monossilabo_3(s):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser um monossilabo de 3 caracteres ou nao,
    de acordo com a gramatica.

    e_monossilabo_3: cadeia de caracteres -> logico
    """

    if len(s) != 3:
        return False

    return (e_consoante(s[0]) and e_vogal(s[1]) and e_consoante_terminal(s[2])) or \
           (e_consoante(s[0]) and e_ditongo(s[1:])) or \
           (e_par_vogais(s[:2]) and e_consoante_terminal(s[2]))


def e_monossilabo_2(s):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser um monossilabo de 2 caracteres ou nao,
    de acordo com a gramatica.

    e_monossilabo_2: cadeia de caracteres -> logico
    """

    if len(s) != 2:
        return False

    return (s in ('AR', 'IR', 'EM', 'UM')) or \
           e_ditongo_palavra(s) or \
           (e_vogal_palavra(s[0]) and s[1] == 'S') or \
           (e_consoante_freq(s[0]) and e_vogal(s[1]))


def e_par_consoantes(c):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser par de consoantes ou nao, de acordo com a gramatica.

    e_par_consoantes: cadeia de caracteres -> logico
    """

    return c in ('BR', 'CR', 'FR', 'GR', 'PR', 'TR', 'VR', 'BL', 'CL', 'FL', 'GL', 'PL')


def e_consoante(c):
    """
    Recebe um caracter e devolve o valor logico associado a
    ser consoante ou nao, de acordo com a gramatica.

    e_consoante: cadeia de caracteres -> logico
    """

    return c in ('B', 'C', 'D', 'F', 'G', 'H', 'J', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'X', 'Z')


def e_consoante_final(c):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser uma consoante final ou nao,
    de acordo com a gramatica.

    e_consoante_final: cadeia de caracteres -> logico
    """

    return c == 'N' or c == 'P' or e_consoante_terminal(c)


def e_consoante_terminal(c):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser uma consoante terminal ou nao,
    de acordo com a gramatica.

    e_consoante_terminal: cadeia de caracteres -> logico
    """

    return c in ('L', 'M', 'R', 'S', 'X', 'Z')


def e_consoante_freq(c):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser uma consoante freq ou nao,
    de acordo com a gramatica.

    e_consoante_freq: cadeia de caracteres -> logico
    """

    return c in ('D', 'L', 'M', 'N', 'P', 'R', 'S', 'T', 'V')


def e_par_vogais(s):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser um par de vogais ou nao, de acordo com a gramatica.

    e_par_vogais: cadeia de caracteres -> logico
    """

    return s == 'IA' or s == 'IO' or e_ditongo(s)


def e_ditongo(s):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser ditongo ou nao, de acordo com a gramatica.

    e_ditongo: cadeia de caracteres -> logico
    """

    return s in ('AE', 'AU', 'EI', 'OE', 'OI', 'IU') or e_ditongo_palavra(s)


def e_ditongo_palavra(s):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser ditongo palavra ou nao, de acordo com a gramatica.

    e_ditongo_palavra: cadeia de caracteres -> logico
    """

    return s in ('AI', 'AO', 'EU', 'OU')


def e_vogal(c):
    """
    Recebe um caracter e devolve o valor logico associado a ser
    vogal ou nao, de acordo com a gramatica.

    e_vogal: cadeia de caracteres -> logico
    """

    return c == 'I' or c == 'U' or e_vogal_palavra(c)


def e_vogal_palavra(c):
    """
    Recebe um caracter e devolve o valor logico associado a ser
    vogal palavra ou nao, de acordo com a gramatica.

    e_vogal_palavra: cadeia de caracteres -> logico
    """

    return c == 'E' or e_artigo_def(c)


def e_artigo_def(c):
    """
    Recebe um caracter e devolve o valor logico associado a ser
    artigo definido ou nao, de acordo com a gramatica.

    e_artigo_def: cadeia de caracteres -> logico
    """

    return c == 'A' or c == 'O'

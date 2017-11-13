# 89455 - Henrique Dias

def e_silaba(caracteres):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico associado
    ao ser silaba ou nao, de acordo com a gramatica.

    e_silaba: string -> logico
    """

    valida('e_silaba', caracteres)

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

    valida('e_monossilabo', caracteres)

    return e_vogal_palavra(caracteres) or \
           e_monossilabo_2(caracteres) or \
           e_monossilabo_3(caracteres)


def e_palavra(caracteres):
    """
    Recebe uma cadeia de caracteres e devolve o valor logico
    associado a ser palavra ou nao, de acordo com a gramatica.

    e_palavra: string -> logico
    """
    def e_palavra_aux(s, silaba_fn):
        """
        Recebe uma cadeia de caracteres e a funcao que verifica a validade
        de uma silaba, e devolve o valor logico associado a cadeia de
        caracteres ser uma palavra ou nao, de acordo com a gramatica.
        Implementa a logica recursiva de e_palavra.

        e_palavra_aux: cadeia de caracteres X funcao da silaba -> logico
        """
        if silaba_fn(s):
            return True

        # a silaba final pode variar entre 2 a 5 caracteres.
        # calcula o numero maximo de caracteres que esta pode ter
        # tendo em conta o numero de caracteres da palavra.
        for i in range(1, min(len(s), 5) + 1):
            if silaba_fn(s[-i:]) and e_palavra_aux(s[:-i], e_silaba):
                return True

        return False

    valida('e_palavra', caracteres)

    if e_monossilabo(caracteres):
        return True

    return e_palavra_aux(caracteres, e_silaba_final)

def valida(nome, arg):
    if not isinstance(arg, str):
        raise ValueError(nome + ':argumento invalido')

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

    return c in ('B', 'C', 'D', 'F', 'G', 'H', 'J', 'L',
                 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'X', 'Z')


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

# fazer conta_repeticoes
def conta_letras(tuplo):
    letras = {}

    for c in tuplo:
        if c not in letras:
            letras[c] = 1
        else:
            letras[c] = letras[c] + 1

    return letras

def cria_palavra_potencial(palavra, letras):
    if not (isinstance(palavra, str) and isinstance(letras, tuple)):
        raise ValueError('cria_palavra_potencial:argumentos invalidos.')

    contagem = conta_letras(letras)

    for c in palavra:
        if not c in contagem:
            raise ValueError('cria_palavra_potencial:a palavra nao e valida.')
        else:
            contagem[c] = contagem[c] - 1
            if contagem[c] < 0:
                raise ValueError('cria_palavra_potencial:a palavra nao e valida.')

    return palavra

def palavra_tamanho(palavra):
    return len(palavra)

def e_palavra_potencial(dados):
    return isinstance(dados, str)

def palavras_potenciais_iguais(palavra1, palavra2):
    return palavra1 == palavra2

def palavra_potencial_menor(palavra1, palavra2):
    return palavra1 < palavra2

def palavra_potencial_para_cadeia(palavra):
    return palavra

def cria_conjunto_palavras():
    return {
        'palavras': [],
        'tamanho': 0
    }

def numero_palavras(conj):
    return conj['tamanho']

def subconjunto_por_tamanho(conj, tamanho):
    if tamanho not in conj['palavras']:
        return '[]'

    return str(conj['palavras'][tamanho])

def insere_ordenado(lst, el):
    if lst == []:
        return [el]
    
    '''
    fi = 0
    la = len(lst) - 1
    indice = -1

    while indice == -1:
        meio = (fi + la) // 2

        if fi == la or fi == la:
            indice = fi
        elif lst[meio] > el:
            la = meio
        else:
            fi = meio

    lst = lst[:indice+1] + [el] + lst[indice+1:]
    '''

    for i in range(len(lst)):
        if lst[i] > el:
            lst = lst[:i] + [el] + lst[i:]
            break

    return lst

def acrescenta_palavra(conj, palavra):
    # TODO: verificar conj?
    if not e_palavra_potencial(palavra):
        raise ValueError('acrescenta_palavra:argumentos invalidos.')

    tamanho = len(palavra)

    if tamanho >= len(conj['palavras']):
        n = tamanho - len(conj['palavras'])
        while n >= 0:
            conj['palavras'] = conj['palavras'] + [[]]
            n = n - 1

    conj['palavras'][tamanho] = insere_ordenado(conj['palavras'][tamanho], palavra)
    conj['tamanho'] = conj['tamanho'] + 1
    
def e_conjunto_palavras(dados):
    if not isinstance(dados, dict):
        return False

    return 'palavras' in dados and isinstance(dados['palavras'], list) and \
        'tamanho' in dados and isinstance(dados['tamanho'], int)

def conjuntos_palavras_iguais(conj1, conj2):
    return conj1 == conj2

def conjunto_palavras_para_cadeia(conj):
    cadeia = ''

    for i in range(len(conj['palavras'])):
        if conj['palavras'][i] != []:
            cadeia = cadeia + str(i) + '->['
            for palavra_potencial in conj['palavras'][i]:
                cadeia = cadeia + palavra_potencial_para_cadeia(palavra_potencial) + ','
            cadeia = cadeia[:-1] + '];'

    if cadeia == '':
        return '[]'

    return '[' + cadeia[:-1] + ']'

def cria_jogador(nome):
    if not isinstance(nome, str):
        raise ValueError('cria_jogador:argumento invalido.')

    return {
        'nome': nome,
        'pontuacao': 0,
        'tentativas': {
            'validas': cria_conjunto_palavras(),
            'invalidas': cria_conjunto_palavras()
        }
    }

def jogador_nome(jogador):
    return jogador['nome']

def jogador_pontuacao(jogador):
    return jogador['pontuacao']

def jogador_palavras_validas(jogador):
    return jogador['tentativas']['validas']

def jogador_palavras_invalidas(jogador):
    return jogador['tentativas']['invalidas']

def adiciona_palavra_valida(jogador, palavra):
    if not (e_jogador(jogador) and e_palavra_potencial(palavra)):
        raise ValueError('adiciona_palavra_valida:argumentos invalidos.')

    acrescenta_palavra(jogador['tentativas']['validas'], palavra)

def adiciona_palavra_invalida(jogador, palavra):
    if not (e_jogador(jogador) and e_palavra_potencial(palavra)):
        raise ValueError('adiciona_palavra_valida:argumentos invalidos.')

    acrescenta_palavra(jogador['tentativas']['invalidas'], palavra)

def e_jogador(x):
    if not isinstance(x, dict):
        return False

    if isinstance(x['nome'], str) and \
        isinstance(x['pontuacao'], int) and \
        isinstance(x['tentativas'], dict) and \
        e_conjunto_palavras(x['tentativas']['validas']) and \
        e_conjunto_palavras(x['tentativas']['invalidas']):
        return True

    return False

def jogador_para_cadeia(jogador):
    return 'JOGADOR ' + jogador['nome'] + \
           ' PONTOS=' + str(jogador['pontuacao']) + \
           ' VALIDAS=' + conjunto_palavras_para_cadeia(jogador['tentativas']['validas']) + \
           ' INVALIDAS=' + conjunto_palavras_para_cadeia(jogador['tentativas']['invalidas'])

def gera_todas_palavras_validas(letras):
    def aux(lst):
        if len(lst) == 1:
            return lst

        if len(lst) == 2:
            return [lst[0]+lst[1], lst[1]+lst[0]]

        els = []

        for i in range(len(lst)):
            for e in aux(lst[:i] + lst[i+1:]):
                els.append(lst[i] + e)

        return els

    combos = list(letras)

    for i in range(len(letras)):
        combos += aux(letras[:i] + letras[i+1:])

    combos += aux(letras)

    conj = cria_conjunto_palavras()

    for combo in combos:
        if e_palavra(combo):
            acrescenta_palavra(conj, cria_palavra_potencial(combo, letras))

    return conj

def guru_mj(letras):
    print('Introduza o nome dos jogadores (-1 para terminar)...')
    jogadores = []

    n = 1

    while n != -1:
        nome = input('JOGADOR ' + str(n) + ' -> ')
        
        if nome == '-1':
            break

        jogadores = jogadores + [cria_jogador(nome)]
        n = n + 1

    palavras = gera_todas_palavras_validas(letras)
    jogada = 1

    while numero_palavras(palavras):
        print('JOGADA', jogada, '- Falta descobrir', numero_palavras(palavras), 'palavras')

        for jogador in jogadores:
            tentativa = input('JOGADOR ' + jogador_nome(jogador) + ' -> ')
            palavra = cria_palavra_potencial(tentativa, letras)
            
            if palavra in subconjunto_por_tamanho(palavras, palavra_tamanho(palavra)):
                print(palavra, ' - palavra VALIDA')
                # valida
            else:
                print(palavra, ' - palavra INVALIDA')
                # invalida





# TESTES
guru_mj(("A", "E", "L"))
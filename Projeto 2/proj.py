# 89455 - Henrique Dias
from parte1 import e_palavra

def contagem_repeticoes(l):
    """
    Recebe um objeto iterável e devolve um dicionário com o número
    de repetições dos seus elementos.

    contagem_repeticoes: list -> dict
    """
    tabela = {}

    for el in l:
        if el in tabela:
            tabela[el] = tabela[el] + 1
        else:
            tabela[el] = 1

    return tabela

def cria_palavra_potencial(palavra, letras):
    if not (isinstance(palavra, str) and isinstance(letras, tuple)):
        raise ValueError('cria_palavra_potencial:argumentos invalidos.')

    for el in [palavra, letras]:
        for c in el:
            if not "Z" >= c >= "A":
                raise ValueError('cria_palavra_potencial:argumentos invalidos.')

    contagem = contagem_repeticoes(letras)

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
    if len(conj['palavras']) < tamanho:
        return []

    return conj['palavras'][tamanho]

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

    for i in range(len(lst) + 1):
        if lst[i] > el:
            lst = lst[:i] + [el] + lst[i:]
            break

    return lst

def acrescenta_palavra(conj, palavra):
    if not (e_conjunto_palavras(conj) and e_palavra_potencial(palavra)):
        raise ValueError('acrescenta_palavra:argumentos invalidos.')

    tamanho = len(palavra)

    if tamanho >= len(conj['palavras']):
        diff = tamanho - len(conj['palavras'])
        while diff >= 0:
            conj['palavras'] = conj['palavras'] + [[]]
            diff = diff - 1

    conj['palavras'][tamanho] = insere_ordenado(conj['palavras'][tamanho], palavra)
    conj['tamanho'] = conj['tamanho'] + 1

def e_conjunto_palavras(dados):
    if not isinstance(dados, dict):
        return False

    # TODO: VERIFICAR TODA A ESTRUTURA
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

    jogador['pontuacao'] = jogador['pontuacao'] + 1
    acrescenta_palavra(jogador['tentativas']['validas'], palavra)

def adiciona_palavra_invalida(jogador, palavra):
    if not (e_jogador(jogador) and e_palavra_potencial(palavra)):
        raise ValueError('adiciona_palavra_valida:argumentos invalidos.')

    acrescenta_palavra(jogador['tentativas']['invalidas'], palavra)

def e_jogador(x):
    return isinstance(x, dict) and \
        isinstance(x['nome'], str) and \
        isinstance(x['pontuacao'], int) and \
        isinstance(x['tentativas'], dict) and \
        e_conjunto_palavras(x['tentativas']['validas']) and \
        e_conjunto_palavras(x['tentativas']['invalidas'])

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

    while True:
        nome = input('JOGADOR ' + str(n) + ' -> ')
        
        if nome == '-1':
            break

        jogadores = jogadores + [cria_jogador(nome)]
        n = n + 1

    palavras = gera_todas_palavras_validas(letras)
    palavras_usadas = cria_conjunto_palavras()
    jogada = 1
    faltam = numero_palavras(palavras)

    while faltam > 0:
        print('JOGADA', jogada, '- Falta descobrir', faltam, 'palavras')

        for jogador in jogadores:
            tentativa = input('JOGADOR ' + jogador_nome(jogador) + ' -> ')
            palavra = cria_palavra_potencial(tentativa, letras)

            if palavra in subconjunto_por_tamanho(palavras, palavra_tamanho(palavra)):
                print(palavra, ' - palavra VALIDA')

                if palavra not in subconjunto_por_tamanho(palavras_usadas, palavra_tamanho(palavra)):
                    adiciona_palavra_valida(jogador, palavra)
                    acrescenta_palavra(palavras_usadas, palavra)
                    faltam = faltam - 1
            else:
                print(palavra, ' - palavra INVALIDA')

        jogada = jogada + 1

# TESTES
guru_mj(("A", "E", "L"))
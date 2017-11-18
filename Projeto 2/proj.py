# 89455 - Henrique Dias
from bisect import insort
from itertools import permutations

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


def acrescenta_palavra(conj, palavra):
    if not (e_conjunto_palavras(conj) and e_palavra_potencial(palavra)):
        raise ValueError('acrescenta_palavra:argumentos invalidos.')

    tamanho = len(palavra)

    if tamanho >= len(conj['palavras']):
        diff = tamanho - len(conj['palavras'])
        while diff >= 0:
            conj['palavras'] = conj['palavras'] + [[]]
            diff = diff - 1

    insort(conj['palavras'][tamanho], palavra)
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

    jogador['pontuacao'] = jogador['pontuacao'] + palavra_tamanho(palavra)
    acrescenta_palavra(jogador['tentativas']['validas'], palavra)


def adiciona_palavra_invalida(jogador, palavra):
    if not (e_jogador(jogador) and e_palavra_potencial(palavra)):
        raise ValueError('adiciona_palavra_valida:argumentos invalidos.')

    jogador['pontuacao'] = jogador['pontuacao'] - palavra_tamanho(palavra)
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
    conj = cria_conjunto_palavras()

    for i in range(1, len(letras) + 1):
        for e in permutations(letras, i):
            combo = ''.join(e)
            if e_palavra(combo):
                acrescenta_palavra(conj, cria_palavra_potencial(combo, letras))

    return conj


def inscrever_jogadores():
    print('Introduza o nome dos jogadores (-1 para terminar)...')

    jogadores = []
    n = 1

    nome = input('JOGADOR ' + str(n) + ' -> ')

    while nome != '-1':
        jogadores.append(cria_jogador(nome))
        n = n + 1
        nome = input('JOGADOR ' + str(n) + ' -> ')

    return jogadores


def seleciona_vencedor(jogadores):
    max_pontuacao = -1
    vencedores = []

    for j in jogadores:
        pontuacao = jogador_pontuacao(j)
        max_pontuacao = max(max_pontuacao, pontuacao)

        if pontuacao != max_pontuacao:
            vencedores = []

        vencedores.append(j)

    return vencedores


def guru_mj(letras):
    jogadores = inscrever_jogadores()

    p_validas = gera_todas_palavras_validas(letras)
    p_usadas = cria_conjunto_palavras()
    faltam = numero_palavras(p_validas)

    jogada = 1

    while faltam:
        for jogador in jogadores:
            print('JOGADA', jogada, '- Falta descobrir', faltam, 'palavras')
            tentativa = input('JOGADOR ' + jogador_nome(jogador) + ' -> ')
            palavra = cria_palavra_potencial(tentativa, letras)

            if palavra in subconjunto_por_tamanho(p_validas, palavra_tamanho(palavra)):
                print(palavra, ' - palavra VALIDA')

                if palavra not in subconjunto_por_tamanho(p_usadas, palavra_tamanho(palavra)):
                    adiciona_palavra_valida(jogador, palavra)
                    acrescenta_palavra(p_usadas, palavra)
                    faltam = faltam - 1
            else:
                adiciona_palavra_invalida(jogador, palavra)
                print(palavra, ' - palavra INVALIDA')

            if faltam < 1:
                break

            jogada = jogada + 1

    vencedores = seleciona_vencedor(jogadores)

    if len(vencedores) > 1:
        print('FIM DE JOGO! O jogo terminou em empate.')
    else:
        print(
            'FIM DE JOGO! O jogo terminou com a vitoria do jogador', jogador_nome(vencedores[0]),
            'com', str(jogador_pontuacao(vencedores[0])), 'pontos.'
        )

    for j in jogadores:
        print(jogador_para_cadeia(j))


# TESTES
guru_mj(("A", "E", "L"))

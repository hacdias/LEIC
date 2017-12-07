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
    """
    Construtor do tipo palavra_potencial. Recebe como argumentos uma cadeia de
    caracteres e um conjunto de letras e devolve uma palavra_potencial.

    Internamente, a palavra_potencial e representada por uma string composta
    por letras maiusculas.

    cria_palavra_potencial: cad. caracteres X tuplo de letras --> palavra_potencial
    """
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
    """
    Recebe como argumento uma palavra_potencial e devolve um inteiro
    com o seu comprimento.

    palavra_tamanho: palavra_potencial --> inteiro
    """
    return len(palavra)

def e_palavra_potencial(dados):
    """
    Reconhecedor do tipo palavra_potencial. Recebe um argumento e devolve
    True caso seja uma palavra_potencial, ou False caso contrário.

    e_palavra_potencial: universal --> lógico
    """
    if not isinstance(dados, str):
        return False

    for c in dados:
        if not "Z" >= c >= "A":
            return False

    return True

def palavras_potenciais_iguais(palavra1, palavra2):
    """
    Recebe dois elementos do tipo palavra_potencial e devolve True
    se forem iguais, ou False caso contrário.

    palavras_potenciais_iguais: palavra_potencial X palavra_potencial --> logico
    """
    return palavra_tamanho(palavra1) == palavra_tamanho(palavra2) and \
        palavra_potencial_para_cadeia(palavra1) == palavra_potencial_para_cadeia(palavra2)

def palavra_potencial_menor(palavra1, palavra2):
    """
    Recebe dois elementos do tipo palavra_potencial e devolve True
    se a primeira for alfabeticamente menor que a segunda, ou False
    caso contrário.

    palavra_potencial_menor: palavra_potencial X palavra_potencial --> logico
    """
    return palavra_potencial_para_cadeia(palavra1) < palavra_potencial_para_cadeia(palavra2)

def palavra_potencial_para_cadeia(palavra):
    """
    Recebe um argumento do tipo palavra_potencial e devolve a cadeia
    de caracteres correspondente.

    palavra_potencial_para_cadeia: palavra_potencial --> cad. caracteres
    """
    return palavra

def cria_conjunto_palavras():
    """
    Construtor do tipo conjunto_palabras, constituido
    pelas palavras e pelo tamanho.

    Internamente, o conjunto_palavras e uma lista de dois elementos:
    - O primeiro e uma lista de listas de elementos do tipo palavra_potencial.
      O indice de cada lista representa o comprimento das palavra_potenciais contidas
      na mesma.
    - O segundo elemento e um inteiro que contem o tamanho do conjunto_palavras.

    cria_conjunto_palavras: --> conjunto_palavras
    """
    return [[], 0]

def numero_palavras(conj):
    """
    Recebe um conjunto_palavras e devolve um inteiro
    que corresponde ao tamanho do conjunto.

    numero_palavras: conjunto_palavras --> inteiro
    """
    return conj[1]

def subconjunto_por_tamanho(conj, tamanho):
    """
    Recebe um conjunto_palavras e um inteiro n e devolve uma lista
    com as palavras_potenciais do tamanho n contidas no conjunto_palavras.

    subconjunto_por_tamanho: conjunto_palavras X inteiro --> lista
    """
    if len(conj[0]) <= tamanho:
        return []

    return conj[0][tamanho]

def acrescenta_palavra(conj, palavra):
    """
    Este modificador recebe um conjunto_palavras e uma palavra_potencial
    e adiciona-a ao conjunto caso ainda nao pertenca ao mesmo.

    acrescenta_palavra: conjunto_palavras X palavra_potencial -->
    """
    if not (e_conjunto_palavras(conj) and e_palavra_potencial(palavra)):
        raise ValueError('acrescenta_palavra:argumentos invalidos.')

    tamanho = palavra_tamanho(palavra)

    diff = tamanho - len(conj[0])
    while diff >= 0:
        conj[0] = conj[0] + [[]]
        diff = diff - 1

    if not palavra_no_conjunto(conj, palavra):
        insort(conj[0][tamanho], palavra)
        conj[1] = conj[1] + 1

def e_conjunto_palavras(arg):
    """
    Recebe apenas um argumento e devolve True caso o argumento
    seja do tipo conjunto_palavras, e False caso contrario.

    e_conjunto_palavras: universal --> logico
    """
    if not (isinstance(arg, list) and
            len(arg) == 2 and
            isinstance(arg[0], list) and
            isinstance(arg[1], int)):
        return False

    for lst in arg[0]:
        if not isinstance(lst, list):
            return False

        for el in lst:
            if not e_palavra_potencial(el):
                return False

    return True

def conjuntos_palavras_iguais(conj1, conj2):
    """
    Este teste compara dois elementos do tipo conjunto_palavras e devolve True
    se forem iguais ou False caso contrário.

    conjuntos_palavras_iguais: conjunto_palavras X conjunto_palavras --> logico
    """
    return conj1 == conj2

def conjunto_palavras_para_cadeia(conj):
    """
    Recebe como argumento um elemento do tipo conjunto_palavras e devolve uma cadeia
    de caracteres que o represente.

    conjunto_palavras_para_cadeia: conjunto_palavras --> cad. caracteres
    """
    cadeia = ''

    for i in range(len(conj[0])):
        if conj[0][i] != []:
            cadeia = cadeia + str(i) + '->['
            for palavra in conj[0][i]:
                cadeia = cadeia + palavra_potencial_para_cadeia(palavra) + ', '
            cadeia = cadeia[:-2] + '];'

    if cadeia == '':
        return '[]'

    return '[' + cadeia[:-1] + ']'

def palavra_no_conjunto(conj, palavra):
    """
    Esta funcao tem como objetivo verificar se a palavra_potencial palavra
    pertence ao conjunto_palavras conj. Isto e feito de forma a evitar
    quebras de barreiras de abstracao no uso da funcionalidade nativa
    do python 'not in'.

    Por exemplo, caso a representacao interna de palavra_potencial for um
    tuplo/lista que contenha, tanto a palavra, como o conjunto que a gerou,
    nao sera possivel utilizar o 'in'.

    palavra_no_conjunto: conjunto_palavras X palavra_potencial --> logico
    """
    for p in subconjunto_por_tamanho(conj, palavra_tamanho(palavra)):
        if palavras_potenciais_iguais(palavra, p):
            return True

    return False

def cria_jogador(nome):
    """
    Recebe uma cadeia de caracteres que corresponde ao nome do jogador,
    e devolve um jogador.

    Internamente, o jogador e representado por uma lista de 4 elementos:
    - O primeiro e uma cadeia de caracteres com o seu nome.
    - O segundo e um inteiro com a sua pontuacao.
    - O terceiro e o quarto contem, respetivamente, as palavras_potenciais validas
      e invalidas em conjuntos_palavras.

    cria_jogador: cad. caracteres --> jogador
    """
    if not isinstance(nome, str):
        raise ValueError('cria_jogador:argumento invalido.')

    return [nome, 0, cria_conjunto_palavras(), cria_conjunto_palavras()]

def jogador_nome(jogador):
    """
    Recebe um elemento do tipo jogador e devolve o seu nome.

    jogador_nome: jogador --> cad. caracteres
    """
    return jogador[0]

def jogador_pontuacao(jogador):
    """
    Recebe um elemento do tipo jogador e devolve a sua pontuacao.

    jogador_pontuacao: jogador --> int
    """
    return jogador[1]

def jogador_palavras_validas(jogador):
    """
    Recebe um elemento do tipo jogador e devolve o conjunto
    das suas palavras validas.

    jogador_palavras_validas: jogador --> conjunto_palavras
    """
    return jogador[2]

def jogador_palavras_invalidas(jogador):
    """
    Recebe um elemento do tipo jogador e devolve o conjunto
    das suas palavras invalidas.

    jogador_palavras_invalidas: jogador --> conjunto_palavras
    """
    return jogador[3]

def adiciona_palavra_valida(jogador, palavra):
    """
    Recebe um elemento do tipo joagador e uma palavra_potencial
    e adiciona-a ao conjunto de palavras validas do jogador,
    atualizando a sua pontuacao.

    adiciona_palavra_valida: jogador X palavra_potencial -->
    """
    if not (e_jogador(jogador) and e_palavra_potencial(palavra)):
        raise ValueError('adiciona_palavra_valida:argumentos invalidos.')

    if not palavra_no_conjunto(jogador_palavras_validas(jogador), palavra):
        jogador[1] = jogador[1] + palavra_tamanho(palavra)
        acrescenta_palavra(jogador[2], palavra)

def adiciona_palavra_invalida(jogador, palavra):
    """
    Recebe um elemento do tipo joagador e uma palavra_potencial
    e adiciona-a ao conjunto de palavras invalidas do jogador,
    atualizando a sua pontuacao.

    adiciona_palavra_invalida: jogador X palavra_potencial -->
    """
    if not (e_jogador(jogador) and e_palavra_potencial(palavra)):
        raise ValueError('adiciona_palavra_invalida:argumentos invalidos.')

    if not palavra_no_conjunto(jogador_palavras_invalidas(jogador), palavra):
        jogador[1] = jogador[1] - palavra_tamanho(palavra)
        acrescenta_palavra(jogador[3], palavra)

def e_jogador(arg):
    """
    Recebe um unico argumento e verifica se e do tipo jogador. Se for,
    devolve True, caso contrario, devolve False.

    e_jogador: universal --> logico
    """
    return (
        isinstance(arg, list) and
        len(arg) == 4 and
        isinstance(arg[0], str) and
        isinstance(arg[1], int) and
        e_conjunto_palavras(arg[2]) and
        e_conjunto_palavras(arg[3])
    )

def jogador_para_cadeia(jogador):
    """
    Esta funcao recebe um elemento do tipo jogador e devolve uma
    cadeia de caracteres que o representa.

    jogador_para_cadeia: jogador --> cad. caracteres
    """
    return 'JOGADOR ' + jogador_nome(jogador) + \
        ' PONTOS=' + str(jogador_pontuacao(jogador)) + \
        ' VALIDAS=' + conjunto_palavras_para_cadeia(jogador_palavras_validas(jogador)) + \
        ' INVALIDAS=' + conjunto_palavras_para_cadeia(jogador_palavras_invalidas(jogador))

def gera_todas_palavras_validas(letras):
    """
    Recebe um tuplo letras e devolve um conjunto_palavras com
    todas as palavras validas de acordo utilizam as letras do tuplo.

    gera_todas_palavras_validas: tuplo de letras -> conjunto_palavras
    """
    conj = cria_conjunto_palavras()

    for i in range(1, len(letras) + 1):
        for perm in permutations(letras, i):
            combo = str.join('', perm)
            if e_palavra(combo):
                acrescenta_palavra(conj, cria_palavra_potencial(combo, letras))

    return conj

def guru_mj(letras):
    """
    Funcao que inicia o jogo GURU multijogador.

    guru_mj: tuplo de letras -->
    """
    def inscrever_jogadores():
        """
        Permite a inscricao dos jogadores no jogo. Devolve
        a lista com os jogadores que se inscreveram.

        inscrever_jogadores: -> lista de jogadores
        """
        print('Introduza o nome dos jogadores (-1 para terminar)...')

        jogadores = []
        n = 1

        nome = input('JOGADOR ' + str(n) + ' -> ')

        while nome != '-1':
            jogadores.append(cria_jogador(nome))
            n = n + 1
            nome = input('JOGADOR ' + str(n) + ' -> ')

        return jogadores

    def termina_jogo(jogadores):
        """
        Recebe uma lista com jogadores, seleciona o vencedor, e imprime
        o resultado final do jogo.

        termina_jogo: lista de jogadores ->
        """
        max_pontuacao = -1
        vencedores = []

        for j in jogadores:
            pontuacao = jogador_pontuacao(j)
            max_pontuacao = max(max_pontuacao, pontuacao)

            if pontuacao == max_pontuacao:
                if len(vencedores) == 0:
                    vencedores = [j]
                elif jogador_pontuacao(vencedores[0]) == max_pontuacao:
                    vencedores = vencedores + [j]
                else:
                    vencedores = [j]

        if len(vencedores) > 1:
            print('FIM DE JOGO! O jogo terminou em empate.')
        else:
            print(
              'FIM DE JOGO! O jogo terminou com a vitoria do jogador', jogador_nome(vencedores[0]),
              'com', str(jogador_pontuacao(vencedores[0])), 'pontos.'
            )

        for j in jogadores:
            print(jogador_para_cadeia(j))

    print('Descubra todas as palavras geradas a partir das letras:')
    print(str(letras))

    jogadores = inscrever_jogadores()
    p_validas = gera_todas_palavras_validas(letras)
    p_usadas = cria_conjunto_palavras()
    faltam = numero_palavras(p_validas)
    jogada, jogador_i, num_jogadores = 1, 0, len(jogadores)

    while faltam:
        print('JOGADA', jogada, '- Falta descobrir', faltam, 'palavras')
        tentativa = input('JOGADOR ' + jogador_nome(jogadores[jogador_i]) + ' -> ')
        palavra = cria_palavra_potencial(tentativa, letras)

        if palavra_no_conjunto(p_validas, palavra):
            print(palavra, '- palavra VALIDA')

            if not palavra_no_conjunto(p_usadas, palavra):
                adiciona_palavra_valida(jogadores[jogador_i], palavra)
                acrescenta_palavra(p_usadas, palavra)
                faltam = faltam - 1
        else:
            print(palavra, '- palavra INVALIDA')
            adiciona_palavra_invalida(jogadores[jogador_i], palavra)

        # Escolhe o indice do proximo jogador
        jogador_i = jogada % num_jogadores
        jogada = jogada + 1

    termina_jogo(jogadores)

guru_mj(('M', 'R', 'O', 'A'))
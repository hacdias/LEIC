def nova_pilha():
    return []

def empurra(pilha, el):
    return pilha + [el]

def topo(pilha):
    return pilha[len(pilha) - 1]

def e_pilha(pilha):
    return isinstance(pilha, list)

def e_pilha_vazia(pilha):
    return len(pilha) == 0

def pilhas_iguais(p1, p2):
    return p1 == p2

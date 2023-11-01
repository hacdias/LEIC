class urna:
    def __init__(self, candidatos):
        self.votos = {}
        for candidato in candidatos:
            self.votos[candidato] = 0

    def vota(self, candidato):
        if candidato in self.votos:
            self.votos[candidato] += 1
        else:
            raise ValueError('vota: candidato inexistente')

    def mostra(self):
        print(self)

    def __repr__(self):
        return str(self.votos)

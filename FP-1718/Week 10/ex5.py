import ex2 as relogio
import ex3 as data

def cria_timestamp(D, M, A, h, m, s):
    return (data.cria_data(D, M, A), relogio.cria_relogio(h, m ,s))

def data_timestamp(t):
    return data[0]

def relogio_timestamp(t):
    return data[1]

def depois(t1, t2):
    if not data.data_anterior(data_timestamp(t1), data_timestamp(t2)):
        return relogio.diferenca_segundos(relogio_timestamp(t1), relogio_timestamp(t2)) < 0
    
    return False

def num_segundos(t):
    dt = data_timestamp(t)
    rt = relogio_timestamp(t)

    return (
        data.ano(dt) * 365 * 24 * 60 +
        data.mes(dt) * 30 * 60 +
        data.dia(dt) * 60
    )

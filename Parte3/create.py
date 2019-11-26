from random import randrange
import datetime

def randomDate(start):
    randomDate = start + datetime.timedelta(minutes=randrange(60 * 24 * 365 * 3))
    return randomDate.strftime("%Y/%m/%d %H:%M:%S")

startDate = datetime.datetime(2018, 9, 20, 13, 00)

users = []
print("-- Populate Utilizador")
for i in range(50):
    users.append(['Email' + str(i), 'password' + str(i)])
    print("INSERT INTO utilizador(email, password) VALUES ('" + 
        users[i][0] + "', '" + users[i][1] + "');")

qualified_users = []
print("\n-- Populate Utilizador Qualificado")
for i in range(0, 50, 2):
    qualified_users.append(['Email' + str(i)])
    print("INSERT INTO utilizador_qualificado(email) VALUES ('" + 
        qualified_users[-1][0] + "');")

regular_users = []
print("\n-- Populate Utilizador Regular")
for i in range(1, 50, 2):
    regular_users.append(['Email' + str(i)])
    print("INSERT INTO utilizador_regular(email) VALUES ('" + 
        regular_users[-1][0] + "');")


iterations = 25
publicLocals = []
counter = 0
print("\n-- Populate Local Publico")
for latitude in range(-iterations, iterations, randrange(7, 10)):
    for longitude in range(-iterations, iterations, randrange(7, 10)):
        publicLocals.append([latitude, longitude, 'LocalPublico' + str(counter)])
        print("INSERT INTO local_publico(latitude, longitude, nome) VALUES (" + 
            str(publicLocals[-1][0]) + ", " + str(publicLocals[-1][1]) + ", '" + publicLocals[-1][2] + "');")
        counter = counter + 1

items = []
print("\n-- Populate Item")
for i in range(30):
    local = randrange(0, len(publicLocals))
    items.append(['Descricao' + str(i), 'Localizacao' + str(i), publicLocals[local][0], publicLocals[local][1]])
    print("INSERT INTO item(descricao, localizacao, latitude, longitude) VALUES ('" + 
        items[i][0] + "', '" + items[i][1] + "', " + str(items[i][2]) + ", " + str(items[i][3]) + ");")

anomalies = []
print("\n-- Populate Anomalia")
for i in range(25):
    box = str(- randrange(0, 20)) + ", " + str(- randrange(0, 20)) + ", " + str(randrange(1, 20)) + ", " + str(randrange(1, 20))
    anomalies.append([box, '1', 'Lingua' + str(i + 1), randomDate(startDate), 'Descricao' + str(i + 1), randrange(0, 2) % 2 ])
    print("INSERT INTO anomalia(zona, imagem, lingua, ts, descricao, tem_anomalia_redacao) VALUES ('" +
        anomalies[i][0] + "', '" + anomalies[i][1] + "', '" + anomalies[i][2] + "', '" + anomalies[i][3] + "', '" + anomalies[i][4] + "', '" + str(anomalies[i][5]) + "');")

anomalies_traduction = []
print("\n-- Populate Anomalia Traducao")
for i in range(1, len(anomalies), randrange(1, 3)):
    box = str(- randrange(0, 20)) + ", " + str(- randrange(0, 20)) + ", " + str(randrange(1, 20)) + ", " + str(randrange(1, 20))
    anomalies_traduction.append([i, box, 'Lingua' + str(i)])
    print("INSERT INTO anomalia_traducao(id, zona2, lingua2) VALUES (" +
        str(anomalies_traduction[-1][0]) + ", '" + anomalies_traduction[-1][1] + "', '" + anomalies_traduction[-1][2] + "');")

duplicate_items = []
print("\n-- Populates Duplicado")
for i in range(25):
    item2 = randrange(2, len(items))
    item1 = randrange(1, item2)
    newItem = [item1, item2]

    while (newItem in duplicate_items):
        item2 = randrange(2, len(items))
        item1 = randrange(1, item2)
        newItem = [item1, item2]
    
    duplicate_items.append(newItem)

    print("INSERT INTO duplicado(item1, item2) VALUES (" +
        str(duplicate_items[-1][0]) + ", " + str(duplicate_items[-1][1]) + ");")

incidences = []
print("\n-- Populates Incidencia")
for i in range(0, len(anomalies)):
    itemID = randrange(1, len(items) + 1)
    email = randrange(1, len(users) + 1)

    incidences.append([i + 1, itemID, 'Email' + str(email)])
    print("INSERT INTO incidencia(anomalia_id, item_id, email) VALUES (" +
        str(incidences[i][0]) + ", " + str(incidences[i][1]) + ", '" + incidences[i][2] + "');")

correction_proposals = []
print("\n-- Populate Proposta de Correcao")
for i in range(0, len(qualified_users), randrange(1, 3)):

    for x in range(1, randrange(1, 5)):
        correction_proposals.append([qualified_users[i][0], x, randomDate(startDate), 'Texto' + str(i)])
        print("INSERT INTO proposta_de_correcao(email, nro, data_hora, texto) VALUES ('" +
            correction_proposals[-1][0] + "', " + str(correction_proposals[-1][1]) + ", '" + correction_proposals[-1][2] + "', '" + correction_proposals[-1][3] + "');")

corrections = []
print("\n-- Populate Correcao")
for i in range(0, len(correction_proposals), randrange(2) + 1):
    corrections.append([correction_proposals[i][0], correction_proposals[i][1], randrange(1, len(anomalies) + 1)])
    print("INSERT INTO correcao(email, nro, anomalia_id) VALUES ('" +
            corrections[-1][0] + "', " + str(corrections[-1][1]) + ", " + str(corrections[-1][2]) + ");")
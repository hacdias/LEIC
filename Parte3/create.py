from random import randrange
import datetime

def randomDate(start):
    randomDate = start + datetime.timedelta(minutes=randrange(60))
    return randomDate.strftime("%Y/%m/%d %H:%M:%S")

startDate = datetime.datetime(2013, 9, 20,13,00)

iterations = 100

print("-- Populate Utilizador")
for i in range(iterations):
    print("INSERT INTO utilizador(email, password) VALUES ('Utilizador" + 
        str(i) + "', 'password" + str(i) + "');")

print("\n-- Populate Utilizador Qualificado")
for i in range(0, iterations, 2):
    print("INSERT INTO utilizador_qualificado(email) VALUES ('Utilizador" + 
        str(i) + "');")

print("\n-- Populate Utilizador Regular")
for i in range(1, iterations, 2):
    print("INSERT INTO utilizador_regular(email) VALUES ('Utilizador" + 
        str(i) + "');")

iterations = 50
locaisPublicos = []
counter = 0

print("\n-- Populate Local Publico")
for latitude in range(-iterations, iterations, 10):
    for longitude in range(-iterations, iterations, 10):
        locaisPublicos.append([latitude, longitude])
        print("INSERT INTO local_publico(latitude, longitude, nome) VALUES (" + 
            str(latitude) + ", " + str(longitude) + ", 'LocalPublico" + str(counter) + "');")
        counter = counter + 1

iterations = 100

print("\n-- Populate Item")
for i in range(iterations):
    print("INSERT INTO item(id, descricao, localizacao, latitude, longitude) VALUES (" + str(i) + ", 'Descricao" + 
            str(i) + "', 'Localizacao" + str(i) + "', " + 
            str(locaisPublicos[i][0]) + ", " + str(locaisPublicos[i][1]) + ");")

print("\n-- Populate Anomalia")
for i in range(iterations):
    box = str(-i) + ", " + str(-i) + ", " + str(i + 1) + ", " + str(i + 1)
    print("INSERT INTO anomalia(id, zona, imagem, lingua, ts, descricao, tem_anomalia_redacao) VALUES (" + str(i) + ", '" +
        box + "', '1', 'Lingua" + str(i) + "', '" + randomDate(startDate) + "', 'Descricao" + str(i) + "', " + str(i % 2 == 0) + ");")

print("\n-- Populate Anomalia Traducao")
for i in range(0, iterations, 3):
    box = str(-i) + ", " + str(-i) + ", " + str(i + 1) + ", " + str(i + 1)
    print("INSERT INTO anomalia_traducao(id, zona2, lingua2) VALUES (" +
        str(i) + ", '" + box + "', 'Lingua" + str(i) + "');")

items = []
print("\n-- Populates duplicado")
for i in range(iterations):
    item2 = randrange(5, 100)
    item1 = randrange(0, item2)
    newItem = [item1, item2]

    while(newItem in items):
        item2 = randrange(5, 100)
        item1 = randrange(0, item2)
        newItem = [item1, item2]
    
    items.append(newItem)

    print("INSERT INTO duplicado(item1, item2) VALUES (" +
        str(item1) + ", " + str(item2) + ");")


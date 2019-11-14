DROP TABLE IF EXISTS utilizador_regular;
DROP TABLE IF EXISTS utilizador_qualificado;
DROP TABLE IF EXISTS utilizador;
DROP TABLE IF EXISTS local_publico;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS anomalia_traducao;
DROP TABLE IF EXISTS anomalia;
DROP TABLE IF EXISTS duplicado;
DROP TABLE IF EXISTS incidencia;
DROP TABLE IF EXISTS proposta_correcao;
DROP TABLE IF EXISTS correcao;

CREATE TABLE utilizador
(
    email VARCHAR NOT NULL PRIMARY KEY,
    password2 VARCHAR NOT NULL
);

CREATE TABLE utilizador_regular (
    email VARCHAR NOT NULL PRIMARY KEY,
    FOREIGN KEY (email) REFERENCES utilizador(email)
);

CREATE TABLE utilizador_qualificado (
    email VARCHAR NOT NULL PRIMARY KEY,
    FOREIGN KEY (email) REFERENCES utilizador(email)
);

CREATE TABLE local_publico (
    latitude FLOAT NOT NULL CHECK (-90 <= latitude AND latitude <= 90),
    longitude FLOAT NOT NULL CHECK (-180 <= longitude AND longitude <= 180),
    nome VARCHAR NOT NULL,
    PRIMARY KEY(latitude, longitude)
);

CREATE TABLE item (
    id SERIAL PRIMARY KEY,
    descricao VARCHAR NOT NULL,
    localizacao VARCHAR NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    FOREIGN KEY (latitude, longitude) REFERENCES local_publico(latitude, longitude)
);

CREATE TABLE anomalia (
    id SERIAL PRIMARY KEY,
    zona BOX NOT NULL,
    imagem BYTEA NOT NULL,
    lingua VARCHAR NOT NULL,
    ts TIMESTAMP NOT NULL,
    descricao VARCHAR NOT NULL,
    tem_anomalia_redacao BOOLEAN NOT NULL
);

CREATE TABLE anomalia_traducao (
    id SERIAL PRIMARY KEY,
    zona2 BOX NOT NULL,
    lingua2 VARCHAR NOT NULL,
    FOREIGN KEY (id) REFERENCES anomalia(id)
);

CREATE TABLE duplicado (
    item1 SERIAL NOT NULL,
    item2 SERIAL NOT NULL CHECK (item1 < item2),
    PRIMARY KEY(item1, item2),
    FOREIGN KEY (item1) REFERENCES item(id),
    FOREIGN KEY (item2) REFERENCES item(id)
);

CREATE TABLE incidencia (
    anomalia_id SERIAL NOT NULL PRIMARY KEY,
    item_id SERIAL NOT NULL,
    email VARCHAR NOT NULL,
    FOREIGN KEY (anomalia_id) REFERENCES anomalia(id),
    FOREIGN KEY (item_id) REFERENCES item(id),
    FOREIGN KEY (email) REFERENCES utilizador(email)
);

CREATE TABLE proposta_correcao (
    email VARCHAR NOT NULL,
    nro INT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    texto VARCHAR NOT NULL,
    PRIMARY KEY (email, nro),
    FOREIGN KEY (email) REFERENCES utilizador_qualificado(email)
);

CREATE TABLE correcao (
    email VARCHAR NOT NULL,
    nro INT NOT NULL,
    anomalia_id SERIAL NOT NULL,
    PRIMARY KEY (email, nro, anomalia_id),
    FOREIGN KEY (email, nro) REFERENCES proposta_correcao(email, nro),
    FOREIGN KEY (anomalia_id) REFERENCES anomalia(id)
);
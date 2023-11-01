DROP TABLE IF EXISTS correcao CASCADE;
DROP TABLE IF EXISTS proposta_de_correcao CASCADE;
DROP TABLE IF EXISTS incidencia CASCADE;
DROP TABLE IF EXISTS duplicado CASCADE;
DROP TABLE IF EXISTS anomalia_traducao CASCADE;
DROP TABLE IF EXISTS anomalia CASCADE;
DROP TABLE IF EXISTS item CASCADE;
DROP TABLE IF EXISTS local_publico CASCADE;
DROP TABLE IF EXISTS utilizador_qualificado CASCADE;
DROP TABLE IF EXISTS utilizador_regular CASCADE;
DROP TABLE IF EXISTS utilizador CASCADE;

CREATE OR REPLACE FUNCTION
utilizador_nao_qualificado (email_utilizador VARCHAR)
RETURNS BOOLEAN
AS $$
BEGIN
	RETURN NOT EXISTS (
        SELECT email
        FROM utilizador_qualificado
        WHERE email = email_utilizador
    );
END;
$$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION
utilizador_nao_regular (email_utilizador VARCHAR)
RETURNS BOOLEAN
AS $$
BEGIN
	RETURN NOT EXISTS (
        SELECT email
        FROM utilizador_regular
        WHERE email = email_utilizador
    );
END;
$$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION
lingua_nao_repetida (idAnomalia INT, lingua2 VARCHAR)
RETURNS BOOLEAN
AS $$
BEGIN
	RETURN NOT EXISTS (
        SELECT *
        FROM anomalia
        WHERE id = idAnomalia AND lingua = lingua2
    );
END;
$$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION
caixa_nao_sobrepoe (idAnomalia INT, zona2 BOX)
RETURNS BOOLEAN
AS $$
BEGIN
	RETURN NOT EXISTS (
        SELECT *
        FROM anomalia
        WHERE id = idAnomalia AND zona && zona2
    );
END;
$$ LANGUAGE PLPGSQL;

CREATE TABLE utilizador
(
    email VARCHAR NOT NULL PRIMARY KEY,
    "password" VARCHAR NOT NULL
);

CREATE TABLE utilizador_regular (
    email VARCHAR NOT NULL PRIMARY KEY CHECK (utilizador_nao_qualificado(email)),
    FOREIGN KEY (email) REFERENCES utilizador(email) ON DELETE CASCADE
);

CREATE TABLE utilizador_qualificado (
    email VARCHAR NOT NULL PRIMARY KEY,
    FOREIGN KEY (email) REFERENCES utilizador(email) ON DELETE CASCADE
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
    FOREIGN KEY (latitude, longitude) REFERENCES local_publico(latitude, longitude) ON DELETE CASCADE
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
    id INT PRIMARY KEY,
    zona2 BOX NOT NULL CHECK(caixa_nao_sobrepoe(id, zona2)),
    lingua2 VARCHAR NOT NULL CHECK(lingua_nao_repetida(id, lingua2)),
    FOREIGN KEY (id) REFERENCES anomalia(id) ON DELETE CASCADE
);

CREATE TABLE duplicado (
    item1 INT NOT NULL,
    item2 INT NOT NULL CHECK (item1 < item2),
    PRIMARY KEY(item1, item2),
    FOREIGN KEY (item1) REFERENCES item(id) ON DELETE CASCADE,
    FOREIGN KEY (item2) REFERENCES item(id) ON DELETE CASCADE
);

CREATE TABLE incidencia (
    anomalia_id INT NOT NULL PRIMARY KEY,
    item_id INT NOT NULL,
    email VARCHAR NOT NULL,
    FOREIGN KEY (anomalia_id) REFERENCES anomalia(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE,
    FOREIGN KEY (email) REFERENCES utilizador(email) ON DELETE CASCADE
);

CREATE TABLE proposta_de_correcao (
    email VARCHAR NOT NULL,
    nro INT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    texto VARCHAR NOT NULL,
    PRIMARY KEY (email, nro),
    FOREIGN KEY (email) REFERENCES utilizador_qualificado(email) ON DELETE CASCADE
);

CREATE TABLE correcao (
    email VARCHAR NOT NULL,
    nro INT NOT NULL,
    anomalia_id INT NOT NULL,
    PRIMARY KEY (email, nro, anomalia_id),
    FOREIGN KEY (email, nro) REFERENCES proposta_de_correcao(email, nro) ON DELETE CASCADE,
    FOREIGN KEY (anomalia_id) REFERENCES incidencia(anomalia_id) ON DELETE CASCADE
);
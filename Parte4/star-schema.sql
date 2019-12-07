DROP TABLE IF EXISTS d_utilizador CASCADE;
DROP TABLE IF EXISTS d_tempo CASCADE;
DROP TABLE IF EXISTS d_local CASCADE;
DROP TABLE IF EXISTS d_lingua CASCADE;
DROP TABLE IF EXISTS f_anomalia CASCADE;

CREATE TABLE d_utilizador (
  id_utilizador SERIAL NOT NULL PRIMARY KEY,
  email VARCHAR(80) NOT NULL,
  tipo VARCHAR(80) NOT NULL
);

CREATE TABLE d_tempo (
  id_tempo SERIAL NOT NULL PRIMARY KEY,
  dia INT NOT NULL,
  dia_da_semana INT NOT NULL,
  semana INT NOT NULL,
  mes INT NOT NULL,
  trimestre INT NOT NULL,
  ano INT NOT NULL
);

CREATE TABLE d_local (
  id_local SERIAL NOT NULL PRIMARY KEY,
  latitude INT NOT NULL,
  longitude INT NOT NULL,
  nome VARCHAR(80) NOT NULL
);

CREATE TABLE d_lingua (
  id_lingua SERIAL NOT NULL PRIMARY KEY,
  lingua VARCHAR(80) NOT NULL
);

CREATE TABLE f_anomalia (
  id_utilizador INT NOT NULL,
  id_tempo INT NOT NULL,
  id_local INT NOT NULL,
  id_lingua INT NOT NULL,
  tipo_anomalia VARCHAR(80) NOT NULL,
  com_proposta BOOLEAN NOT NULL,
  FOREIGN KEY (id_utilizador) REFERENCES d_utilizador(id_utilizador) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (id_tempo) REFERENCES d_tempo(id_tempo) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (id_local) REFERENCES d_local(id_local) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (id_lingua) REFERENCES d_lingua(id_lingua) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO d_utilizador(email, tipo)
  SELECT email, 'regular' as tipo FROM utilizador_regular;

INSERT INTO d_utilizador(email, tipo)
  SELECT email, 'qualificado' as tipo FROM utilizador_qualificado;

INSERT INTO d_tempo(dia, dia_da_semana, semana, mes, trimestre, ano)
  SELECT EXTRACT(day FROM ts) AS dia,
    EXTRACT(dow FROM ts) AS dia_da_semana,
    EXTRACT(week FROM ts) AS semana,
    EXTRACT(month FROM ts) AS mes,
    FLOOR((EXTRACT(month FROM ts) - 1) / 4) + 1 AS trimestre,
    EXTRACT(year FROM ts) AS ano
  FROM anomalia
  ORDER BY dia, dia_da_semana, semana, mes, trimestre, ano;

INSERT INTO d_local(latitude, longitude, nome)
  SELECT latitude, longitude, nome FROM local_publico
  ORDER BY nome;

INSERT INTO d_lingua(lingua)
  SELECT lingua FROM anomalia
  UNION
  SELECT lingua2 AS lingua FROM anomalia_traducao
  ORDER BY lingua;

INSERT INTO f_anomalia(id_utilizador, id_tempo, id_local, id_lingua, tipo_anomalia, com_proposta)
  SELECT id_utilizador, id_tempo, id_local, id_lingua, tipo_anomalia, com_proposta
  FROM (SELECT id AS anomalia_id,
        lingua,
        EXTRACT(day FROM ts) AS dia,
      EXTRACT(dow FROM ts) AS dia_da_semana,
      EXTRACT(week FROM ts) AS semana,
      EXTRACT(month FROM ts) AS mes,
      FLOOR((EXTRACT(month FROM ts) - 1) / 4) + 1 AS trimestre,
      EXTRACT(year FROM ts) AS ano,
        CASE
              WHEN tem_anomalia_redacao
                THEN 'redacao'
                ELSE 'traducao'
          END AS tipo_anomalia,
      CASE
        WHEN EXISTS (select *
              from correcao c
              where c.anomalia_id = anomalia.id)
        THEN True
        ELSE False
      END AS com_proposta
      FROM anomalia) AS anomalias NATURAL JOIN
      incidencia NATURAL JOIN
      (SELECT id AS item_id,
        latitude, longitude
      FROM item) AS items NATURAL JOIN
      d_utilizador NATURAL JOIN
      d_lingua NATURAL JOIN
      d_local NATURAL JOIN
      d_tempo
  ORDER BY anomalia_id;
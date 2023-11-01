--1
SELECT nome
FROM (SELECT id AS item_id, latitude, longitude
	FROM item) AS i NATURAL JOIN
	(SELECT id AS anomalia_id
	FROM anomalia) AS a NATURAL JOIN
	(SELECT item_id, anomalia_id
	FROM incidencia) AS inc NATURAL JOIN
	(SELECT latitude, longitude, nome
	FROM local_publico) AS lp
GROUP BY nome
HAVING COUNT (anomalia_id) >= ALL (
	SELECT COUNT(anomalia_id)
	FROM (SELECT id AS item_id, latitude, longitude
		FROM item) AS i NATURAL JOIN
		(SELECT id AS anomalia_id
		FROM anomalia) AS a NATURAL JOIN
		(SELECT item_id, anomalia_id
		FROM incidencia) AS inc
	GROUP BY latitude, longitude
)
ORDER BY nome;

--2
SELECT email
FROM (SELECT email
	FROM utilizador_regular) AS u NATURAL JOIN
	(SELECT anomalia_id, email
	FROM incidencia) AS inc NATURAL JOIN
	(SELECT id AS anomalia_id, ts
	FROM anomalia
	WHERE ts BETWEEN '2019-01-01 00:00:00' AND '2019-06-30 23:59:59') AS a NATURAL JOIN
	(SELECT id AS anomalia_id
	FROM anomalia_traducao) AS at
GROUP BY email
HAVING COUNT (anomalia_id) >= ALL (
	SELECT COUNT(anomalia_id)
	FROM (SELECT email
		FROM utilizador_regular) AS u NATURAL JOIN
		(SELECT anomalia_id, email
		FROM incidencia) AS inc NATURAL JOIN
		(SELECT id AS anomalia_id, ts
		FROM anomalia
		WHERE ts BETWEEN '2019-01-01 00:00:00' AND '2019-06-30 23:59:59') AS a NATURAL JOIN
		(SELECT id AS anomalia_id
		FROM anomalia_traducao) AS at
	GROUP BY email
)
ORDER BY email;

--3
SELECT email
FROM (SELECT email, COUNT(nome) AS nlocais
	FROM (SELECT anomalia_id, item_id, email
		FROM incidencia) AS inc NATURAL JOIN
		(SELECT id AS anomalia_id
		FROM anomalia
		WHERE ts BETWEEN '2019-01-01 00:00:00' AND '2019-12-31 23:59:59') AS a NATURAL JOIN
		(SELECT id AS item_id, latitude, longitude
		FROM item
		WHERE latitude > 39.336775) AS i NATURAL JOIN
		(SELECT latitude, longitude, nome
		FROM local_publico) AS lp
	GROUP BY email) AS c 
WHERE nlocais = (SELECT COUNT(*)
	FROM local_publico
	WHERE latitude > 39.336775)
ORDER BY email;

--4
SELECT email
FROM (SELECT anomalia_id, item_id, email
	FROM incidencia) AS inc NATURAL JOIN
	(SELECT id AS anomalia_id
	FROM anomalia
	WHERE extract(year from ts) = extract(year from NOW())) AS a NATURAL JOIN
	(SELECT id AS item_id, latitude, longitude
	FROM item
	WHERE latitude < 39.336775) AS i
WHERE (email, anomalia_id) NOT IN (
	SELECT email, anomalia_id
	FROM correcao
	);
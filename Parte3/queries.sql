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
HAVING COUNT (*) >= ALL (
	SELECT COUNT(*)
	FROM (SELECT id AS item_id, latitude, longitude
		FROM item) AS i NATURAL JOIN
		(SELECT id AS anomalia_id
		FROM anomalia) AS a NATURAL JOIN
		(SELECT item_id, anomalia_id
		FROM incidencia) AS inc
	GROUP BY latitude, longitude
)

--2 --COMO POR 1st SEMESTRE?
SELECT email
FROM (SELECT email AS e
    FROM utilizador) AS user NATURAL JOIN
    (SELECT id AS anomalia_id
	FROM anomalia) AS a NATURAL JOIN
	(SELECT anomalia_id
	FROM incidencia) AS inc
WHERE YEAR('2019')
GROUP BY email
HAVING COUNT (*) >= ALL (
	SELECT COUNT(*)
	FROM (SELECT email AS e
        FROM utilizador) AS user NATURAL JOIN
        (SELECT id AS anomalia_id
		FROM anomalia) AS a NATURAL JOIN
		(SELECT anomalia_id
		FROM incidencia) AS inc
	GROUP BY email



--3




--4
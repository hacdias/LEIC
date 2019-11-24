--1
SELECT latitude, longitude
FROM (SELECT id AS item_id, latitude, longitude
    FROM item) AS i NATURAL JOIN
    (SELECT id AS anomalia_id
    FROM anomalia) AS a NATURAL JOIN
    (SELECT item_id, anomalia_id
    FROM incidencia) AS inc
ORDER BY latitude, longitude;

--2 --???
    SELECT email
    FROM anomalia NATURAL JOIN utilizador_regular NATURAL JOIN proposta_correcao
    WHERE DATE(data_hora) = '1-1-2019' TO '31-5-2019',
        GROUP BY email
        HAVING COUNT(anomalia) > 1 


--3



--4
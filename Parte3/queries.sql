--1 ?? DÚVIDA COMO RELACIONAR ANOMALIA COM LOCAL PÚBLICO--
    SELECT lp.name 
    FROM anomalia AS a  NATURAL JOIN local_publico AS lp
    WHERE GROUP BY name 
        HAVING COUNT(anomalia) > 1

--2 --???
    SELECT email
    FROM anomalia NATURAL JOIN utilizador_regular NATURAL JOIN proposta_correcao
    WHERE DATE(data_hora) = '1-1-2019' TO '31-5-2019',
        GROUP BY email
        HAVING COUNT(anomalia) > 1 


--3



--4
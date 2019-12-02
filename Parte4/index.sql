--1
CREATE INDEX data_hora_index on proposta_de_correcao(data_hora) 
    using primary KEY

DROP INDEX data_hora_index


--2
CREATE INDEX email_incidencia_index on incidencia(anomalia_id) 
    using dynamic hashing

DROP INDEX email_incidencia_index

--3
CREATE INDEX email_correcao_index on correcao(anomalia_id) 
    using B+-Tree

DROP INDEX email_correcao_index

--4(Multiple key access(?))



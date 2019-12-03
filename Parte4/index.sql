-- 1
-- 1.1

CREATE INDEX data_hora_index ON proposta_de_correcao
    USING BTREE(data_hora);

-- 1.2

CREATE INDEX data_hora_index ON proposta_de_correcao
    USING BTREE(data_hora);

-- 2
CREATE INDEX email_incidencia_index ON incidencia 
    USING HASH(anomalia_id);

-- 3
-- 3.1

CREATE INDEX email_correcao_index ON correcao 
    USING BTREE(anomalia_id);

-- 3.2

CREATE INDEX email_correcao_index ON correcao
    USING BTREE(anomalia_id);

-- 4

CREATE INDEX anomalia_timestamp ON anomalia(ts)
    USING BTREE
    WHERE tem_anomalia_redacao = True;

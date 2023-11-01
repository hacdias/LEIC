-- Using Cube
SELECT tipo_anomalia, lingua, dia_da_semana, COUNT(*)
FROM f_anomalia NATURAL JOIN d_lingua NATURAL JOIN d_tempo
GROUP BY CUBE(tipo_anomalia, lingua, dia_da_semana);

-- Using Unions
SELECT tipo_anomalia, lingua, dia_da_semana, COUNT(*)
FROM f_anomalia NATURAL JOIN d_lingua NATURAL JOIN d_tempo
GROUP BY tipo_anomalia, lingua, dia_da_semana
    UNION
SELECT tipo_anomalia, lingua, null, COUNT(*)
FROM f_anomalia NATURAL JOIN d_lingua NATURAL JOIN d_tempo
GROUP BY tipo_anomalia, lingua
    UNION
SELECT tipo_anomalia, null, dia_da_semana, COUNT(*)
FROM f_anomalia NATURAL JOIN d_lingua NATURAL JOIN d_tempo
GROUP BY tipo_anomalia, dia_da_semana
    UNION
SELECT null, lingua, dia_da_semana, COUNT(*)
FROM f_anomalia NATURAL JOIN d_lingua NATURAL JOIN d_tempo
GROUP BY lingua, dia_da_semana
    UNION
SELECT tipo_anomalia, null, null, COUNT(*)
FROM f_anomalia NATURAL JOIN d_lingua NATURAL JOIN d_tempo
GROUP BY tipo_anomalia
    UNION
SELECT null, lingua, null, COUNT(*)
FROM f_anomalia NATURAL JOIN d_lingua NATURAL JOIN d_tempo
GROUP BY lingua
    UNION
SELECT null, null, dia_da_semana, COUNT(*)
FROM f_anomalia NATURAL JOIN d_lingua NATURAL JOIN d_tempo
GROUP BY dia_da_semana
    UNION
SELECT null, null, null, COUNT(*)
FROM f_anomalia NATURAL JOIN d_lingua NATURAL JOIN d_tempo
ORDER BY count DESC;
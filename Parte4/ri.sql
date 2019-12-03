-- RI-1

CREATE OR REPLACE FUNCTION
caixa_nao_sobrepoe_procedure ()
RETURNS TRIGGER
AS $$
BEGIN
	IF (EXISTS (
      SELECT *
      FROM anomalia
      WHERE id = new.id AND zona && new.zona2
    )) THEN
      RAISE EXCEPTION 'A zona 2 sobrepõe-se a outra zona.';
    END IF;
    RETURN new;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS caixa_nao_sobrepoe_insert ON anomalia_traducao;
DROP TRIGGER IF EXISTS caixa_nao_sobrepoe_update ON anomalia_traducao;

CREATE TRIGGER caixa_nao_sobrepoe_insert BEFORE INSERT ON anomalia_traducao
FOR EACH ROW EXECUTE PROCEDURE caixa_nao_sobrepoe_procedure();

CREATE TRIGGER caixa_nao_sobrepoe_update BEFORE UPDATE ON anomalia_traducao
FOR EACH ROW EXECUTE PROCEDURE caixa_nao_sobrepoe_procedure();

-- RI-4
-- TODO: ver

CREATE OR REPLACE FUNCTION
check_utilizador_procedure ()
RETURNS TRIGGER
AS $$
BEGIN
	IF (NOT EXISTS (
      SELECT *
      FROM (
        SELECT email FROM utilizador_qualificado
          UNION ALL
        SELECT email FROM utilizador_regular
      ) s
      WHERE email = new.email
    )) THEN
      RAISE EXCEPTION 'O utilizador % tem que ser qualificado ou regular.', new.email;
    END IF;
    RETURN new;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS check_utilizador ON utilizador;

CREATE TRIGGER check_utilizador AFTER INSERT ON utilizador
FOR EACH ROW EXECUTE PROCEDURE check_utilizador_procedure();

-- RI-5

CREATE OR REPLACE FUNCTION
check_utilizador_qualificado_procedure ()
RETURNS TRIGGER
AS $$
BEGIN
	IF (EXISTS (
      SELECT email
      FROM utilizador_regular
      WHERE email = new.email
    )) THEN
      RAISE EXCEPTION 'O utilizador % já é um utilizador regular.', new.email;
    END IF;
    RETURN new;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS check_utilizador_qualificado ON utilizador_qualificado;

CREATE TRIGGER check_utilizador_qualificado BEFORE INSERT ON utilizador_qualificado
FOR EACH ROW EXECUTE PROCEDURE check_utilizador_qualificado_procedure();

-- RI-6

CREATE OR REPLACE FUNCTION
check_utilizador_regular_procedure ()
RETURNS TRIGGER
AS $$
BEGIN
	IF (EXISTS (
      SELECT email
      FROM utilizador_qualificado
      WHERE email = new.email
    )) THEN
      RAISE EXCEPTION 'O utilizador % já é um utilizador qualificado.', new.email;
    END IF;
    RETURN new;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS check_utilizador_regular ON utilizador_regular;

CREATE TRIGGER check_utilizador_regular BEFORE INSERT ON utilizador_regular
FOR EACH ROW EXECUTE PROCEDURE check_utilizador_regular_procedure();

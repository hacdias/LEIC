-- RI-1

CREATE OR REPLACE FUNCTION
caixa1_nao_sobrepoe_procedure ()
RETURNS TRIGGER
AS $$
BEGIN
	IF (EXISTS (
      SELECT *
      FROM anomalia_traducao
      WHERE id = new.id AND zona2 && new.zona
    )) THEN
      RAISE EXCEPTION 'A zona 1 sobrepõe-se a outra zona.';
    END IF;
    RETURN new;
END;
$$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION
caixa2_nao_sobrepoe_procedure ()
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

DROP TRIGGER IF EXISTS caixa_nao_sobrepoe_zona1 ON anomalia;
DROP TRIGGER IF EXISTS caixa_nao_sobrepoe_zona2 ON anomalia_traducao;

CREATE TRIGGER caixa_nao_sobrepoe_zona1 BEFORE UPDATE ON anomalia
FOR EACH ROW EXECUTE PROCEDURE caixa1_nao_sobrepoe_procedure();

CREATE TRIGGER caixa_nao_sobrepoe_zona2 BEFORE INSERT OR UPDATE ON anomalia_traducao
FOR EACH ROW EXECUTE PROCEDURE caixa2_nao_sobrepoe_procedure();

-- RI-4

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

CREATE CONSTRAINT TRIGGER check_utilizador AFTER INSERT OR UPDATE OR DELETE ON utilizador
DEFERRABLE INITIALLY DEFERRED
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

CREATE TRIGGER check_utilizador_qualificado BEFORE INSERT OR UPDATE ON utilizador_qualificado
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

CREATE TRIGGER check_utilizador_regular BEFORE INSERT OR UPDATE ON utilizador_regular
FOR EACH ROW EXECUTE PROCEDURE check_utilizador_regular_procedure();

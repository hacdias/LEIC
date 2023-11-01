<?php

$host = "db.ist.utl.pt";
$user ="ist189535";
$password = "kkha7371";
$dbname = $user;

$db = new PDO("pgsql:host=$host;dbname=$dbname", $user, $password);
$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

function getTable ($table, $what) {
  global $db;

  $sql = "SELECT " . join(", ", $what) . " FROM " . $table . ";";
  $result = $db->prepare($sql);
  $result->execute();

  $arr = [];

  foreach ($result as $key => $value) {
    $arr[$key] = $value;
  }

  return $arr;
}

function insert ($table, $values) {
  global $db;
  
  $columns = array_keys($values);
  $vars = array_map(function ($column) {
    return ":" . $column;
  }, $columns);

  $colums = join(", ", array_keys($values));

  $sql = "INSERT INTO " . $table . " (" . join(", ", $columns) . ") VALUES (" . join(", ", $vars). ");";
  $result = $db->prepare($sql);

  $toExecute = [];

  foreach ($values as $key => $value) {
    $toExecute[":".$key] = $value;
  }

  $result->execute($toExecute);
}

function getLocals () {
  return getTable("local_publico", ["nome", "latitude", "longitude"]);
}

function removeLocal ($latitude, $longitude) {
  global $db;
  
  $sql = "DELETE FROM local_publico WHERE latitude = :latitude AND longitude = :longitude;";
  $result = $db->prepare($sql);
  $result->execute([':latitude' => $latitude, ':longitude' => $longitude]);
}

function insertLocal ($name, $latitude, $longitude) {
  global $db;
  $db->beginTransaction();

  try {
    insert("local_publico", [
      "nome" => $name,
      "latitude" => $latitude,
      "longitude" => $longitude
    ]);
    $db->commit();
  } catch (PDOException $e) {
    $db->rollBack();
    throw $e;
  }
}

function getItems () {
  return getTable("item", ["id", "descricao", "localizacao", "latitude", "longitude"]);
}

function insertItem ($descricao, $localizacao, $latitude, $longitude) {
  global $db;
  $db->beginTransaction();

  try {
    insert("item", [
      "descricao" => $descricao,
      "localizacao" => $localizacao,
      "latitude" => $latitude,
      "longitude" => $longitude
    ]);

    $db->commit();
  } catch (PDOException $e) {
    $db->rollBack();
    throw $e;
  }
}

function removeItem ($id) {
  global $db;
  $sql = "DELETE FROM item WHERE id = :id;";
  $result = $db->prepare($sql);
  $result->execute([':id' => $id]);
}

function getAnomalies () {
  return getTable("anomalia", ["id, zona, imagem, lingua, ts, descricao, tem_anomalia_redacao"]);
}

function getAnomaliesAround ($latitude, $longitude, $dlatitude, $dlongitude) {
  global $db;
  $latMin = $latitude - $dlatitude;
  $latMax = $latitude + $dlatitude;
  $lonMin = $longitude - $dlongitude;
  $lonMax = $longitude + $dlongitude;

  $sql = "SELECT anomalia_id AS id, zona, imagem, lingua, descricao, ts, tem_anomalia_redacao
    FROM incidencia NATURAL JOIN
      (SELECT latitude, longitude, nome
      FROM local_publico) AS lp NATURAL JOIN
    (SELECT id AS anomalia_id, ts, zona, imagem, lingua, descricao, tem_anomalia_redacao
    FROM anomalia
    WHERE NOW() - ts <= interval '3 months'
      AND NOW() - ts >= interval '0 seconds') AS a NATURAL JOIN
      (SELECT id AS item_id, latitude, longitude
      FROM item
      WHERE latitude BETWEEN :latmin AND :latmax
        AND longitude BETWEEN :lonmin AND :lonmax) AS i
    ORDER BY anomalia_id;";


  
  $result = $db->prepare($sql);
  $result->execute([
    ':latmin' => $latMin,
    ':latmax' => $latMax,
    ':lonmin' => $lonMin,
    ':lonmax' => $lonMax
  ]);

  $arr = [];

  foreach ($result as $key => $value) {
    $arr[$key] = $value;
  }

  return $arr;
}

function insertAnomaly ($zona, $imagem, $lingua, $ts, $descricao, $tem_anomalia_redacao) {
  global $db;
  $db->beginTransaction();

  try {
    insert("anomalia", [
      "zona" => $zona,
      "imagem" => $imagem,
      "lingua" => $lingua,
      "ts" => $ts,
      "descricao" => $descricao,
      "tem_anomalia_redacao" => $tem_anomalia_redacao
    ]);
    $db->commit();
  } catch (PDOException $e) {
    $db->rollBack();
    throw $e;
  }
}

function insertDuplicate ($item1, $item2) {
  global $db;
  $db->beginTransaction();

  try {
    insert("duplicado", [
      "item1" => $item1,
      "item2" => $item2
    ]);
    $db->commit();
  } catch (PDOException $e) {
    $db->rollBack();
    throw $e;
  }
}

function insertIncidence ($anomaly, $item, $email) {
  global $db;
  $db->beginTransaction();

  try {
    insert("incidencia", [
      "anomalia_id" => $anomaly,
      "item_id" => $item,
      "email" => $email
    ]);
    $db->commit();
  } catch (PDOException $e) {
    $db->rollBack();
    throw $e;
  }
}

function removeAnomaly ($id) {
  global $db;
  $db->beginTransaction();

  try {
    $sql = "DELETE FROM anomalia WHERE id = :id;";
    $result = $db->prepare($sql);
    $result->execute([':id' => $id]);

    $db->commit();
  } catch (PDOException $e) {
    $db->rollBack();
    throw $e;
  }
}

function getRegularUsers () {
  return getTable("utilizador_regular", ["email"]);
}

function getQualifiedUsers () {
  return getTable("utilizador_qualificado", ["email"]);
}

function getUsers () {
  return getTable("utilizador", ["email"]);
}

function getIncidences () {
  return getTable("incidencia", ["anomalia_id"]);
}

function getCorrections () {
  global $db;
  $sql = "SELECT email, nro, anomalia_id, texto, data_hora FROM correcao NATURAL JOIN proposta_de_correcao;";
  $result = $db->prepare($sql);
  $result->execute();

  $arr = [];

  foreach ($result as $key => $value) {
    $arr[$key] = $value;
  }

  return $arr;
}

function removeCorrection ($email, $nro, $anomaly) {
  global $db;
  $db->beginTransaction();

  try {
    $sql = "DELETE FROM correcao WHERE email = :email AND nro = :nro AND anomalia_id = :anomaly;";
    $result = $db->prepare($sql);
    $result->execute([':email' => $email, ':nro' => $nro, ':anomaly' => $anomaly]);

    $sql = "DELETE FROM proposta_de_correcao WHERE email = :email AND nro = :nro";
    $result = $db->prepare($sql);
    $result->execute([':email' => $email, ':nro' => $nro]);

    $db->commit();
  } catch (PDOException $e) {
    $db->rollBack();
    throw $e;
  }
}

function getCorrectionProposals () {
  return getTable("proposta_de_correcao", ["data_hora", "nro", "email", "texto"]);
}

function insertCorrection ($email, $anomaly, $text) {
  global $db;
  $db->beginTransaction();

  try {
    $sql = "SELECT max(nro) FROM proposta_de_correcao WHERE email = :email;";
    $result = $db->prepare($sql);
    $result->execute([':email' => $email]);

    $nro = $result->fetch()['max'];
    if ($nro == null) {
      $nro = 0;
    } else {
      $nro = $nro + 1;
    }
    

    insert("proposta_de_correcao", [
      "texto" => $text,
      "data_hora" => date(DATE_RFC2822),
      "nro" => $nro,
      "email" => $email
    ]);

    insert("correcao", [
      "anomalia_id" => $anomaly,
      "nro" => $nro,
      "email" => $email
    ]);

    $db->commit();
  } catch (PDOException $e) {
    $db->rollBack();
    throw $e;
  }
}

function editCorrectionProposal ($email, $nro, $text) {
  global $db;
  $db->beginTransaction();

  try {
    $sql = "UPDATE proposta_de_correcao SET texto = :texto, data_hora = NOW() WHERE email = :email AND nro = :nro;";
    $result = $db->prepare($sql);
    $result->execute([':email' => $email, ':nro' => $nro, ':texto' => $text]);

    $db->commit();
  } catch (PDOException $e) {
    $db->rollBack();
    throw $e;
  }
}

function getAnomaliesBetween ($latitude1, $longitude1, $latitude2, $longitude2) {
  global $db;
  $sql = "SELECT anomalia_id AS id, ts, zona, imagem, lingua, descricao, tem_anomalia_redacao
  FROM incidencia NATURAL JOIN
    (SELECT latitude, longitude, nome
    FROM local_publico) AS lp NATURAL JOIN
    (SELECT id AS anomalia_id, ts, zona, imagem, lingua, descricao, tem_anomalia_redacao
    FROM anomalia) AS a NATURAL JOIN
    (SELECT id AS item_id, latitude, longitude
    FROM item
    WHERE latitude BETWEEN :latitude1 AND :latitude2
      AND longitude BETWEEN :longitude1 AND :longitude2) AS i
  ORDER BY id;";

  $result = $db->prepare($sql);
  $result->execute([':latitude1' => $latitude1, ':latitude2' => $latitude2, ':longitude1' => $longitude1, ':longitude2' => $longitude2]);

  return $result;
}
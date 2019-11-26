<?php

function getDB () {
  $host = "db.ist.utl.pt";
  $user ="ist189535";
  $password = "kkha7371";
  $dbname = $user;

  $db = new PDO("pgsql:host=$host;dbname=$dbname", $user, $password);
  $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
  return $db;
}

function getTable ($table, $what) {
  $db = getDB();

  $sql = "SELECT " . join(", ", $what) . " FROM " . $table . ";";
  $result = $db->prepare($sql);
  $result->execute();

  return $result;
}

function insert ($table, $values) {
  $db = getDB();

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

function removeLocal ($name) {
  $db = getDB();

  $sql = "DELETE FROM local_publico WHERE nome = :name;";
  $result = $db->prepare($sql);
  $result->execute([':name' => $name]);
}

function insertLocal ($name, $latitude, $longitude) {
  return insert("local_publico", [
    "nome" => $name,
    "latitude" => $latitude,
    "longitude" => $longitude
  ]);
}

function getItems () {
  return getTable("item", ["id", "descricao", "localizacao", "latitude", "longitude"]);
}

function insertItem ($descricao, $localizacao, $latitude, $longitude) {
  return insert("item", [
    "descricao" => $descricao,
    "localizacao" => $localizacao,
    "latitude" => $latitude,
    "longitude" => $longitude
  ]);
}

function removeItem ($id) {
  $db = getDB();

  $sql = "DELETE FROM item WHERE id = :id;";
  $result = $db->prepare($sql);
  $result->execute([':id' => $id]);
}

function getAnomalies () {
  return getTable("anomalia", ["id, zona, imagem, lingua, ts, descricao, tem_anomalia_redacao"]);
}

function insertAnomaly ($descricao, $localizacao, $latitude, $longitude) {
  return insert("anomalia", [
    "zona" => $zona,
    "imagem" => $imagem,
    "lingua" => $lingua,
    "ts" => $ts,
    "descricao" => $descricao,
    "tem_anomalia_redacao" => $tem_anomalia_redacao
  ]);
}

function removeAnomaly ($id) {
  $db = getDB();

  $sql = "DELETE FROM anomalia WHERE id = :id;";
  $result = $db->prepare($sql);
  $result->execute([':id' => $id]);
}

function getRegularUsers () {
  return getTable("utilizador_regular", ["email"]);
}

function getQualifiedUsers () {
  return getTable("utilizador_qualificado", ["email"]);
}

function getAnomaliesBetween ($latitude1, $longitude1, $latitude2, $longitude2) {
  $db = getDB();

  $sql = "SELECT item_id, anomalia_id, email, latitude, longitude, nome
  FROM incidencia NATURAL JOIN 
    (SELECT latitude, longitude, nome
    FROM local_publico) AS lp NATURAL JOIN
    (SELECT id AS item_id, latitude, longitude
    FROM item
    WHERE latitude BETWEEN :latitude1 AND :latitude2
      AND longitude BETWEEN :longitude1 AND :longitude2) AS i
  ORDER BY anomalia_id;";

  $result = $db->prepare($sql);
  $result->execute([':latitude1' => $latitude1, ':latitude2' => $latitude2, ':longitude1' => $longitude1, ':longitude2' => $longitude2]);

  return $result;
}
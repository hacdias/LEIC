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

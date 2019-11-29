<?php require __DIR__ . '/lib.php'; ?>
<html>
<head>
  <title>Incidência</title>
  <meta charset="UTF-8">
</head>
<body>
  <p><a href="./index.php">← Página incial</a></p>

  <?php
    $items = [];
    $users = [];
    $anomalies = [];

    try {
      $users = getUsers();
      $items = getItems();
      $anomalies = getAnomalies();
    } catch (PDOException $e) {
      echo "<p>Não foi possível obter os dados.</p>";
      echo "<p style='color:red'>$e;</p></body></html>";
      die(1);
    }
  ?>

  <form method="GET" action="./incidences_insert.php">
    <label>Anomalia</label>
    <select required name="anomaly">
      <?php foreach ($anomalies as $row): ?>
        <option value="<?=$row['id']?>">ID: <?=$row['id']?> - <?=$row['descricao']?></option>
      <?php endforeach; ?> 
    </select>
    <br>
    <br>

    <label>Item</label>
    <select required name="item">
      <?php foreach ($items as $row): ?>
        <option value="<?=$row['id']?>">ID: <?=$row['id']?> - <?=$row['descricao']?></option>
      <?php endforeach; ?> 
    </select>
    <br>
    <br>

    <label>Utilizador</label>
    <select required name="email">
      <?php foreach ($users as $row): ?>
        <option value="<?=$row['email']?>"><?=$row['email']?></option>
      <?php endforeach; ?> 
    </select>
    <br>
    <br>

    <input type="submit" value="Criar incidência">
  </form>
</body>
</html>
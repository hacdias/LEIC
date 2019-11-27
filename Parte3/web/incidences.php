<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <title>Incidência</title>
</head>
<body>
  <p><a href="./index.php">← Página incial</a></p>
  <?php $users = getUsers(); ?>
  <?php $items = getItems(); ?>
  <?php $anomalies = getAnomalies(); ?>

  <form method="GET" action="./incidences_insert.php">
    <label>Anomalia</label>
    <select name="anomaly">
      <?php foreach ($anomalies as $row): ?>
        <option value="<?=$row['id']?>"><?=$row['descricao']?></option>
      <?php endforeach; ?> 
    </select>
    <br>

    <label>Item</label>
    <select name="item">
      <?php foreach ($items as $row): ?>
        <option value="<?=$row['id']?>"><?=$row['descricao']?></option>
      <?php endforeach; ?> 
    </select>
    <br>

    <label>Utilizador</label>
    <select name="email">
      <?php foreach ($users as $row): ?>
        <option value="<?=$row['email']?>"><?=$row['email']?></option>
      <?php endforeach; ?> 
    </select>
    <br>

    <input type="submit" value="Criar incidência">
  </form>
</body>
</html>
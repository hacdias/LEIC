<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <title>Duplicates</title>
</head>
<body>
  <p><a href="./index.php">← Página incial</a></p>

  <?php
    $items = [];

    try {
      $items = getItems(); 
    } catch (PDOException $e) {
      echo "<p>Não foi possível obter os dados.</p>";
      echo "<p style='color:red'>$e;</p></body></html>";
      die(1);
    }
  ?>

  <form method="GET" action="./duplicates_insert.php">
    <label>Item 1</label>
    <select name="item1">
      <?php foreach ($items as $row): ?>
        <option value="<?=$row['id']?>"><?=$row['descricao']?></option>
      <?php endforeach; ?> 
    </select>
    <br>

    <label>Item 2</label>
    <select name="item2">
      <?php foreach ($items as $row): ?>
        <option value="<?=$row['id']?>"><?=$row['descricao']?></option>
      <?php endforeach; ?> 
    </select>
    <br>

    <input type="submit" value="Marcar Duplicado">
  </form>
</body>
</html>
<?php 
  try {
    require __DIR__ . '/lib.php';
   } catch (PDOException $e) {
    echo "<p style='color:red'>Não foi possível ligar à base de dados!</p>";
    die(1);
   }
?>

<html>
<head>
  <title>Duplicates</title>
  <meta charset="UTF-8">
</head>
<body>
  <p><a href="./index.php">← Página incial</a></p>

  <?php
    $items = [];

    try {
      $items = getItems(); 
    } catch (PDOException $e) {
      echo "<p>Não foi possível obter os dados.</p>";
      echo "<p style='color:red'>$e->getMessage();</p></body></html>";
      die(1);
    }
  ?>

  <form method="GET" action="./duplicates_insert.php">
    <label>Item 1</label>
    <select required name="item1">
      <?php foreach ($items as $row): ?>
        <option value="<?=$row['id']?>">ID: <?=$row['id']?> - <?=$row['descricao']?></option>
      <?php endforeach; ?> 
    </select>
    <br>

    <label>Item 2</label>
    <select required name="item2">
      <?php foreach ($items as $row): ?>
        <option value="<?=$row['id']?>">ID: <?=$row['id']?> - <?=$row['descricao']?></option>
      <?php endforeach; ?> 
    </select>
    <br>
    <br>

    <input type="submit" value="Marcar Duplicado">
  </form>
</body>
</html>
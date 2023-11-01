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
    <meta charset="UTF-8">
  </head>
<body>
  <p><a href="./index.php">← Página incial</a></p>
  <?php
    $items = [];

    try {
      $items = getItems();
    } catch (PDOException $e) {
      echo "<p>Não foi possível obter os items.</p>";
      echo "<p style='color:red'>$e->getMessage();</p></body></html>";
      die(1);
    }
  ?>

  <form method="GET" action="./items_insert.php">
    <h2>Novo Item</h2>
    <input required type="text" name="descricao" placeholder="Descrição" />
    <input required type="text" name="localizacao" placeholder="Localização" />
    <input required type="number" min="-90" max="90" name="latitude" placeholder="Latitude" />
    <input required type="number" min="-180" max="180" name="longitude" placeholder="Longitude" />
    <input type="submit" value="Criar" />
  </form>

  <table>
    <tr>
      <th>ID</td>
      <th>Descrição</th>
      <th>Localização</th>
      <th>Latitude</th>
      <th>Longitude</th>
      <th></th>
    </tr>
    <?php foreach ($items as $row): ?>
      <tr>
        <td><?=$row['id']?></td>
        <td><?=$row['descricao']?></td>
        <td><?=$row['localizacao']?></td>
        <td><?=$row['latitude']?></td>
        <td><?=$row['longitude']?></td>
        <td><a href="./items_remove.php?id=<?=$row['id']?>">Remover</a></td>
      </tr>
    <?php endforeach; ?>
  </table>
</body>
</html>

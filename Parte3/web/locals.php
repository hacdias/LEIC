<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
  <head>
    <meta char="UTF-8">
  </head>
<body>
  <p><a href="./index.php">← Página incial</a></p>
  <?php
    $locals = [];

    try {
      $locals = getLocals();
    } catch (PDOException $e) {
      echo "<p>Não foi possível obter os locais.</p>";
      echo "<p style='color:red'>$e;</p></body></html>";
      die(1);
    }
  ?>

  <form method="GET" action="./locals_insert.php">
    <h2>Novo Local</h2>
    <input type="text" name="name" placeholder="Nome" />
    <input type="number" min="-90" max="90" name="latitude" placeholder="Latitude" />
    <input type="number" min="-180" max="180" name="longitude" placeholder="Longitude" />
    <input type="submit" value="Criar" />
  </form>

  <table>
    <tr>
      <th>Nome</td>
      <th>Latitude</th>
      <th>Longitude</th>
      <th></th>
    </tr>
    <?php foreach ($locals as $row): ?>
      <tr>
        <td><?=$row['nome']?></td>
        <td><?=$row['latitude']?></td>
        <td><?=$row['longitude']?></td>
        <td><a href="./locals_remove.php?name=<?=$row['nome']?>&latitude=<?=$row['latitude']?>&longitude=<?=$row['longitude']?>">Remover</a></td>
      </tr>
    <?php endforeach; ?>
  </table>
</body>
</html>

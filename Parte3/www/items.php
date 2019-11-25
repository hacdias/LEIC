<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<body>
  <?php $items = getItems(); ?>

  <form method="GET" action="./items_insert.php">
    <h2>Novo Item</h2>
    <input type="text" name="descricao" placeholder="Descrição" />
    <input type="text" name="localizacao" placeholder="Localização" />
    <input type="number" min="-90" max="90" name="latitude" placeholder="Latitude" />
    <input type="number" min="-180" max="180" name="longitude" placeholder="Longitude" />
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

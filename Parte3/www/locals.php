<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<body>
  <?php $locals = getLocals(); ?>

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
        <td><a href="./locals_remove.php?name=<?=$row['nome']?>">Remover</a></td>
      </tr>
    <?php endforeach; ?>
  </table>
</body>
</html>

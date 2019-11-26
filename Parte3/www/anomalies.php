<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<body>
  <?php $anomalies = getAnomalies();?>

  <form method="GET" action="./anomalies_insert.php">
    <h2>Nova Anomalia</h2>
    <input type="text" name="zona" placeholder="Zona x1, y1, x2, y2" />
    <input type="text" name="imagem" placeholder="Imagem (bits)" />
    <input type="text" name="lingua" placeholder="Lingua" />
    <input type="text" name="ts" placeholder="YYYY/MM/DD HH:MM:SS" />
    <input type="text" name="descricao" placeholder="Descrição" />
    <input type="text" name="tem_anomalia_redacao" placeholder="True / False" />
    <input type="submit" value="Criar" />
  </form>

  <form method="GET" action="./anomalies_between_locals.php">
    <h2>Anomalias entre:</h2>
    <input type="number" min="-90" max="90" name="latitude1" placeholder="Latitude 1" />
    <input type="number" min="-180" max="180" name="longitude1" placeholder="Longitude 1" />
    <input type="number" min="-90" max="90" name="latitude2" placeholder="Latitude 2" />
    <input type="number" min="-180" max="180" name="longitude2" placeholder="Longitude 2" />
    <input type="submit" value="Procurar" />
  </form>

  <table>
    <tr>
      <th>ID</td>
      <th>Zona</th>
      <th>Imagem</th>
      <th>Língua</th>
      <th>TS</th>
      <th>Descrição</th>
      <th>Tem Anomalia Redação</th>
      <th></th>
    </tr>
    <?php foreach ($anomalies as $row): ?>
      <tr>
        <td><?=$row['id']?></td>
        <td><?=$row['zona']?></td>
        <td><?=$row['imagem']?></td>
        <td><?=$row['lingua']?></td>
        <td><?=$row['ts']?></td>
        <td><?=$row['descricao']?></td>
        <td><?=$row['tem_anomalia_redacao']?></td>
        <td><a href="./anomalies_remove.php?id=<?=$row['id']?>">Remover</a></td>
      </tr>
    <?php endforeach; ?>
  </table>
</body>
</html>
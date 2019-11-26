<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<body>
    <?php
    $local1 = explode(",", $_REQUEST['local1']);
    $local2 = explode(",", $_REQUEST['local2']);

    $anomalies = getAnomaliesBetween($local1[0], $local1[1], $local2[0], $local2[1]); 
    ?>

  <table>
    <tr>
      <th>Item ID</td>
      <th>Anomalia ID</th>
      <th>Email</th>
      <th>Latitude</th>
      <th>Longitude</th>
      <th>Nome Local</th>
      <th></th>
    </tr>
    <?php foreach ($anomalies as $row): ?>
      <tr>
        <td><?=$row['anomalia_id']?></td>
        <td><?=$row['item_id']?></td>
        <td><?=$row['email']?></td>
        <td><?=$row['latitude']?></td>
        <td><?=$row['longitude']?></td>
        <td><?=$row['nome']?></td>
      </tr>
    <?php endforeach; ?>
  </table>
</body>
</html>
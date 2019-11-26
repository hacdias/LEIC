<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<body>
    <?php
    $latitude1 = $_REQUEST['latitude1'];
    $longitude1 = $_REQUEST['longitude1'];
    $latitude2 = $_REQUEST['latitude2'];
    $longitude2 = $_REQUEST['longitude2'];
    $anomalies = getAnomaliesBetween($latitude1, $longitude1, $latitude2, $longitude2); 
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
<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<body>
  <p><a href="./index.php">← Página incial</a></p>
  <?php $users = getRegularUsers(); ?>

  <table style="float: left">
    <tr>
      <th>Email Utilizador Regular</td>
    </tr>
    <?php foreach ($users as $row): ?>
      <tr>
        <td><?=$row['email']?></td>
      </tr>
    <?php endforeach; ?>
  </table>

  <?php $users = getQualifiedUsers(); ?>

  <table style="float: left">
    <tr>
      <th>Email Utilizador Qualificado</td>
    </tr>
    <?php foreach ($users as $row): ?>
      <tr>
        <td><?=$row['email']?></td>
      </tr>
    <?php endforeach; ?>
  </table>
</body>
</html>

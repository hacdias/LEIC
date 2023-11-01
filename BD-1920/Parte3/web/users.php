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
    $regularUsers = [];
    $qualifiedUsers = [];

    try {
      $regularUsers = getRegularUsers();
      $qualifiedUsers = getQualifiedUsers();
    } catch (PDOException $e) {
      echo "<p>Não foi possível obter os utilizadores.</p>";
      echo "<p style='color:red'>$e->getMessage();</p></body></html>";
      die(1);
    }
  ?>

  <table style="float: left">
    <tr>
      <th>Email Utilizador Regular</td>
    </tr>
    <?php foreach ($regularUsers as $row): ?>
      <tr>
        <td><?=$row['email']?></td>
      </tr>
    <?php endforeach; ?>
  </table>

  <table style="float: left">
    <tr>
      <th>Email Utilizador Qualificado</td>
    </tr>
    <?php foreach ($qualifiedUsers as $row): ?>
      <tr>
        <td><?=$row['email']?></td>
      </tr>
    <?php endforeach; ?>
  </table>
</body>
</html>

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
  <p><a href="./corrections.php">← Página anterior</a></p>
  <?php
    $user = $_REQUEST['user'];
    $anomaly = $_REQUEST['anomaly'];
    $text = $_REQUEST['text'];
    try {
      insertCorrection($user, $anomaly, $text);
      echo "<p>Correção e proposta de correção adicionadas com sucesso.</p>";
    } catch (PDOException $e) {
      echo "<p>Ocorreu um erro:</p>";
      echo "<p style='color:red'>$e;</p>";
    }
  ?>
</body>
</html>

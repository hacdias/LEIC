<?php require __DIR__ . '/lib.php'; ?>
<html>
<head>
  <meta charset="UTF-8">
</head>
<body>
  <p><a href="./corrections.php">← Página anterior</a></p>
  <?php
    $email = $_REQUEST['email'];
    $nro = $_REQUEST['nro'];
    $anomaly = $_REQUEST['anomaly'];

    try {
      removeCorrection($email, $nro, $anomaly);
      echo "<p>Correção e a sua proposta removidas com sucesso.</p>";
    } catch (PDOException $e) {
      echo "<p>Ocorreu um erro:</p>";
      echo "<p style='color:red'>$e;</p>";
    }
  ?>
</body>
</html>

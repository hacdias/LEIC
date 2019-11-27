<?php require __DIR__ . '/lib/lib.php'; ?>
<html>
<head>
  <meta http-equiv="Refresh" content="5; url=./corrections.php" />
</head>
<body>
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
  <p>Será redirecionado dentro de 5 segundos.</p>
</body>
</html>
